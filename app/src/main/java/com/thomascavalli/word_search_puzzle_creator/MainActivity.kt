//
//  MainActivity.kt
//
//  Creative Commons Attribution 4.0 International Public License
//  Creative Commons may be contacted at creativecommons.org.
//
//  Copyright (c) 2024 Thomas Cavalli
//  Thomas Cavalli may be contacted at thomascavalli.com
//
//  List of What Changed (by Who and When):
//  1) Created by Thomas Cavalli on 06/01/2024.
//  2) Added Scroll view to the main activity by Thomas Cavalli on 06/06/2024.
//  3) Created first diagonal grid by Thomas Cavalli on 06/09/2024.
//  4) Created coding growable grid by Thomas Cavalli on 06/10/2024.
//  5) Created coding to insert a word into the grid by Thomas Cavalli on 06/17/2024.
//  6) Started coding to create the Word Search Puzzle by Thomas Cavalli on 06/18/2024.
//  7)
//  8)

package com.thomascavalli.word_search_puzzle_creator

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thomascavalli.word_search_puzzle_creator.ui.theme.Word_Search_Puzzle_CreatorTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object Constants {
    const val CREATOR = " Word Search Puzzle Creator "
    const val CREATOR1 = " CC_BY_4.0, Created by Thomas Cavalli on 06/01/2024. "
    const val CREATOR2 = " What Changed by who and when.  Thank you. "
}
object Word {
    var word: String = ""
    var letters: List<Char> = listOf()
    var selected: Boolean = false
    var selectRow: List<Int> = listOf()  // 0..<rowMaxSize
    var selectCol: List<Int> = listOf()  // 0..<columnMaxSize
    var priority: Int = 0
    var growTop: Int = 0
    var growBottom: Int = 0
    var growLeft: Int = 0
    var growRight: Int = 0
    var startRow: Int = 0
    var startCol: Int = 0
    var index: Int = 0
    var direction: Int = 0
    var selectedItems: List<Int> = listOf()
}
object Global {
    var letters = mutableListOf<Char>()
    var rowMaxSize: Int = 25
    var columnMaxSize: Int = 15
    var aWord: Word = Word
    var bWord: Word = Word
    val dRow = intArrayOf (-1,-1, 0, 1, 1, 1, 0,-1)
    val dCol = intArrayOf ( 0, 1, 1, 1, 0,-1,-1,-1)
    var bGrid = mutableListOf<Char>()
    var aGrid = mutableListOf<Char>()
    var Words = mutableListOf<Word>()
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val readWords = SearchWordReader(this).readSearchWords()
        val searchWordSaver = SearchWordSaver(this)
        setContent {
            Word_Search_Puzzle_CreatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier
                        .padding(innerPadding)
                        .border(
                            width = 5.dp,
                            color = isSystemADarkTheme(),
                            shape = RectangleShape
                        )
                        .fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Word Search Puzzle Creator")
                        EditableWordList(
                            searchWordSaver = searchWordSaver,
                            words = readWords
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun DrawGrid(words: MutableList<String>, newGrid: Boolean = false) {
    Box(
        modifier = Modifier
            .border(
                width = 2.dp,
                color = isSystemADarkTheme(),
                shape = RectangleShape
            )
            .padding(2.dp)
    ) {
        if (words.isEmpty()) {
            RandomLetterGrid(rows = 25, columns = 15)
        }else{
            if (newGrid) {
                val sortedWords = words.sortedBy { it.length }
                val lengthOfLongestWord = sortedWords.last().length
                var numberOfWords = 0
                for (searchWord in sortedWords) {
                    if (searchWord.length == lengthOfLongestWord) {
                        numberOfWords++
                    }
                }
                val biggestWords = sortedWords.takeLast(numberOfWords)
                val luckyWord = biggestWords.random()
                Global.rowMaxSize = luckyWord.length
                Global.columnMaxSize = luckyWord.length
                Global.letters.clear()   //removeAll { true }
                val count = Global.rowMaxSize * Global.columnMaxSize
                repeat(count) {
                    Global.letters.add('*')
                }
//                var rowCounter = Global.rowMaxSize - 1
//                var columnCounter = Global.columnMaxSize - 1
//                for (letter in luckyWord) {
//                    Global.letters[rowCounter * Global.columnMaxSize + columnCounter] = letter
//                    rowCounter--
//                    columnCounter--
//                }
                val theWord = luckyWord.toList()
                Global.bWord = Word
                makeWord(Global.rowMaxSize - 1, Global.columnMaxSize - 1, theWord, 7)
                Global.Words.clear()
                Global.Words.add(Global.bWord)
            }

            val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            val darkTheme = isSystemInDarkTheme()
            val colorScheme = when {
                dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
                dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
                darkTheme -> MaterialTheme.colorScheme
                else -> MaterialTheme.colorScheme
            }
            // Display the grid of letters
            LazyVerticalGrid(
                columns = GridCells.Fixed(Global.columnMaxSize),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(Global.letters) { letter ->
                    LetterCard(letter = letter, colorScheme = colorScheme)
                }
            }
        }
    }
}
@Composable
fun RandomLetterGrid(rows: Int = 5, columns: Int = 5) {
    Global.letters = generateRandomLetters(rows * columns)
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> MaterialTheme.colorScheme
        else -> MaterialTheme.colorScheme
    }
    Text("Random Grid")
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(Global.letters) { letter ->
            LetterCard(letter = letter, colorScheme = colorScheme)
        }
    }
}

@Composable
fun LetterCard(letter: Char, colorScheme: ColorScheme) {
    Text(
        text = letter.toString(),
        fontSize = 20.sp,
        color = colorScheme.onSurfaceVariant,
        fontFamily = FontFamily(typeface = Typeface.SANS_SERIF),
        modifier = Modifier
    )
}
@Composable
fun EditableWordList(searchWordSaver: SearchWordSaver, words: MutableList<String>) {
    var newWord: String by remember { mutableStateOf("") }
    var addOrConfirm: String by remember { mutableStateOf("Add") }
    var clearOrCancel: String by remember { mutableStateOf("Clear") }
    var newGrid: Boolean by remember { mutableStateOf(true) }
    var showView: String by remember { mutableStateOf("Words") }
    Column(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .fillMaxHeight(fraction = 1f),
    ) {
        if (showView == "Words") {
            newGrid = true
            newGrid = false
            newGrid = true
            Button(
                modifier = Modifier.padding(2.dp).align(Alignment.CenterHorizontally),
                onClick = {
                    showView = "Grid"
                }) {
                Text("Create Word Search")
            }
            // Input field for adding new words
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newWord,
                    onValueChange = { newWord = it },
                    label = { Text("Add Words here.") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.padding(8.dp))

                Button(onClick = {
                    if (addOrConfirm == "Add") {
                        if (newWord.isNotBlank()) {
                            words.add(newWord)
                            words.sort()
                            newWord = ""
                            searchWordSaver.saveSearchWords(words)
                            newGrid = true
                        }
                    } else {
                        newWord = "A"
                        newWord = ""
                        words.removeAll { true }
                        searchWordSaver.saveSearchWords(words)
                        addOrConfirm = "Add"
                        clearOrCancel = "Clear"
                        newGrid = true
                    }
                }) {
                    Text(addOrConfirm)
                }
                Spacer(modifier = Modifier.padding(8.dp))

                Button(onClick = {
                    if (clearOrCancel == "Clear") {
                        addOrConfirm = "Confirm"
                        clearOrCancel = "Cancel"
                    } else {
                        clearOrCancel = "Clear"
                        addOrConfirm = "Add"
                    }
                }) {
                    Text(clearOrCancel)
                }
            }
            // Display the list of words
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(words) { word ->
                    WordItem(word = word, onDelete = {
                        words.remove(word)
                        searchWordSaver.saveSearchWords(words)
                        newWord = "A"
                        newWord = ""
                        addOrConfirm = "Add"
                        clearOrCancel = "Clear"
                    })
                }
            }
        }

        if (showView == "Grid") {
            Column(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Button(onClick = {
                    showView = "Words"
                }) {
                    Text("< Back to Word List")
                }
                Row(
                    modifier = Modifier
                        .padding(2.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Top
                ) {
                    Button(onClick = {
                        growTop()
                        newWord = "A"
                        newWord = ""
                        newGrid = false
                        newGrid = true
                        newGrid = false
                    }) {
                        Text("Grow Top")
                    }
                    Button(onClick = {
                        growBottom()
                        newWord = "A"
                        newWord = ""
                        newGrid = false
                        newGrid = true
                        newGrid = false
                    }) {
                        Text("Bottom")
                    }
                    Button(onClick = {
                        growLeft()
                        newWord = "A"
                        newWord = ""
                        newGrid = false
                        newGrid = true
                        newGrid = false
                    }) {
                        Text("Left")
                    }
                    Button(onClick = {
                        growRight()
                        newWord = "A"
                        newWord = ""
                        newGrid = false
                        newGrid = true
                        newGrid = false
                    }) {
                        Text("Right")
                    }
                }
                DrawGrid(words = words, newGrid = newGrid)
            }
        }
    }
}

@Composable
fun WordItem(word: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = word, modifier = Modifier.weight(1f))
            Button(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}
@Composable
fun isSystemADarkTheme(): Color {
    return if (isSystemInDarkTheme())
        Color.White //of border
    else
        Color.DarkGray
}

private fun generateRandomLetters(count: Int): MutableList<Char> {
    val chars = mutableListOf<Char>()
    repeat(count) {
        chars.add(('A'..'Z').random())
    }
    return chars
}

private fun growTop() {
    repeat(Global.columnMaxSize) {
        Global.letters.add(0, '*')
    }
    Global.rowMaxSize++
}
private fun growBottom() {
    repeat(Global.columnMaxSize) {
        Global.letters.add('*')
    }
    Global.rowMaxSize++
}
private fun growLeft() {
    for (row in 0 until Global.rowMaxSize) {
        Global.letters.add((Global.columnMaxSize * row + row), '*')
    }
    Global.columnMaxSize++
}
private fun growRight() {
    for (row in 1 until Global.rowMaxSize) {
        Global.letters.add((Global.columnMaxSize * row + (row - 1)), '*')
    }
    Global.letters.add('*')
    Global.columnMaxSize++
}
private fun makeWord(bRow: Int, bColumn: Int, theWord: List<Char>, bDirection: Int) {
    var aRow = bRow
    var aColumn = bColumn
    Global.bWord.startRow = aRow
    Global.bWord.startCol = aColumn
    Global.bWord.direction = bDirection
    Global.bWord.selectRow = listOf()
    Global.bWord.selectCol = listOf()
    Global.bWord.selectedItems = listOf()
    for (letter in theWord) {
        Global.bWord.word += letter.toString()
        Global.bWord.letters += letter
        Global.bWord.selectedItems += aRow * Global.columnMaxSize + aColumn
        Global.letters[aRow * Global.columnMaxSize + aColumn] = letter
        Global.bWord.selectRow += aRow
        Global.bWord.selectCol += aColumn
        aRow += Global.dRow[bDirection]
        aColumn += Global.dCol[bDirection]
    }
}
class SearchWordSaver(private val context: Context) {
    private val fileName = "search_words.txt"
    fun saveSearchWords(searchWords: List<String>) {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            file.createNewFile()
        }
        FileOutputStream(file, false).use { outputStream ->
            for (searchWord in searchWords) {
                outputStream.write(searchWord.toByteArray())
                outputStream.write("\n".toByteArray())
            }
        }
    }
}

class SearchWordReader(private val context: Context) {
    private val fileName = "search_words.txt"
    fun readSearchWords(): MutableList<String> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            return mutableListOf()
        }
        val searchWords = mutableListOf<String>()
        FileInputStream(file).use { inputStream ->
            inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    searchWords.add(line)
                }
            }
        }
        return searchWords
    }
}