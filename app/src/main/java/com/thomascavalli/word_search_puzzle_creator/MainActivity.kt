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
//  4) Started coding growable grid by Thomas Cavalli on 06/09/2024.
//  5)
//  6)

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //val rowMaxSize = 25
        //val columnMaxSize = 15
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
fun DrawGrid(words: MutableList<String>) {
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
            val rowMaxSize = luckyWord.length
            val columnMaxSize = luckyWord.length
            val count = rowMaxSize * columnMaxSize
            val letters = mutableListOf<Char>()
            repeat(count) {
                letters.add('*')
            }
            val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            val darkTheme = isSystemInDarkTheme()
            val colorScheme = when {
                dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
                dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
                darkTheme -> MaterialTheme.colorScheme
                else -> MaterialTheme.colorScheme
            }
            var rowCounter = rowMaxSize - 1
            var columnCounter = columnMaxSize - 1
            for (letter in luckyWord) {
                letters[rowCounter * columnMaxSize + columnCounter] = letter
                rowCounter--
                columnCounter--
            }


            // Display the grid of letters
            LazyVerticalGrid(
                columns = GridCells.Fixed(columnMaxSize),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(letters) { letter ->
                    LetterCard(letter = letter, colorScheme = colorScheme)
                }
            }
        }
    }
}
@Composable
fun RandomLetterGrid(rows: Int = 5, columns: Int = 5) {
    val letters = generateRandomLetters(rows * columns)
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val darkTheme = isSystemInDarkTheme()
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> MaterialTheme.colorScheme
        else -> MaterialTheme.colorScheme
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(letters) { letter ->
            LetterCard(letter = letter, colorScheme = colorScheme)
        }
    }
}

@Composable
fun LetterCard(letter: Char, colorScheme: androidx.compose.material3.ColorScheme) {
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
    Column(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.5f),
    ) {
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
                    }
                }else{
                    newWord = "A"
                    newWord = ""
                    words.removeAll { true }
                    searchWordSaver.saveSearchWords(words)
                    addOrConfirm = "Add"
                    clearOrCancel = "Clear"
                }
            }) {
                    Text(addOrConfirm)
            }
            Spacer(modifier = Modifier.padding(8.dp))

            Button(onClick = {
                if (clearOrCancel == "Clear") {
                    addOrConfirm = "Confirm"
                    clearOrCancel = "Cancel"
                }else{
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
    Column(modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth()
        .fillMaxHeight(fraction = 1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        DrawGrid(words = words)

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

private fun generateRandomLetters(count: Int): List<Char> {
    val chars = mutableListOf<Char>()
    repeat(count) {
        chars.add(('A'..'Z').random())
    }
    return chars
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