// app/src/main/java/com/example/flashcardapp/screens/EditSetScreen.kt

package com.example.flashcardapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.flashcardapp.navigation.Screen
import com.example.flashcardapp.data.FlashcardDao
import com.example.flashcardapp.model.Flashcard
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


//Similar to createsetscreen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSetScreen(
    navController: NavController,
    flashcardDao: FlashcardDao,
    originalSetName: String  //Name of the set that is being edited
) { //gets userId
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    //Gets all the flashcards that are in this set from the database
    val cards by flashcardDao
        .getFlashcardsForSet(originalSetName, userId)
        .collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var setName by rememberSaveable { mutableStateOf(originalSetName) }

    Scaffold(
        topBar = { //top bar with back arrow to previous page and save set button
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Edit \"$originalSetName\"") },
                actions = {
                    TextButton(onClick = {
                        if (setName.trim() != originalSetName) { //if the user renames the set change the name in the database
                            scope.launch {
                                flashcardDao.renameSet(
                                    oldName = originalSetName,
                                    newName = setName.trim(),
                                    userId  = userId
                                )
                            }
                        }
                        navController.popBackStack()
                        navController.navigate(
                            Screen.SetDetail.route.replace("{setName}", setName.trim()) //after saving the set go back to the setdetail page with the new changes
                        )
                    }) {
                        Text("Save")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor    = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            //Rename field
            OutlinedTextField(
                value = setName,
                onValueChange = { setName = it },
                label = { Text("Set Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            //List existing cards with edit & remove functionality
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(cards) { c ->
                    Card(
                        modifier  = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            var q by remember(c) { mutableStateOf(c.question) }
                            var a by remember(c) { mutableStateOf(c.answer) }
                            //editable question fiels
                            OutlinedTextField(
                                value = q,
                                onValueChange = { newQ ->
                                    q = newQ
                                    scope.launch { //changes in database
                                        flashcardDao.insertFlashcard(c.copy(question = newQ))
                                    }
                                },
                                label = { Text("Question") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            //editable answer field
                            OutlinedTextField(
                                value = a,
                                onValueChange = { newA ->
                                    a = newA
                                    scope.launch { //changes in database
                                        flashcardDao.insertFlashcard(c.copy(answer = newA))
                                    }
                                },
                                label = { Text("Answer") },
                                modifier = Modifier.fillMaxWidth()
                            )


                            //Delete button allows you to remove a flashcard from the set
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = {
                                    scope.launch { flashcardDao.deleteFlashcard(c) }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                    Spacer(Modifier.width(4.dp))
                                    Text("Remove")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            //Add a new flashcard
            OutlinedButton(
                onClick = {
                    scope.launch {
                        flashcardDao.insertFlashcard(
                            Flashcard(
                                question = "",
                                answer   = "",
                                setName  = setName.trim(),
                                userId   = userId
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Card")
            }
        }
    }
}
