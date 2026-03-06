package com.adriantache.projecttracker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.adriantache.projecttracker.ui.theme.ProjectTrackerTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

// TODO: create Google login state and move UI to custom view
class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth

        val credentialManager = CredentialManager.create(this)

        setContent {
            ProjectTrackerTheme {
                val scope = rememberCoroutineScope()
                var userEmail by remember { mutableStateOf(auth.currentUser?.email ?: "Not signed in") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(modifier = Modifier.background(Color.White)) {
                            if (auth.currentUser == null) {
                                Button(onClick = {
                                    scope.launch {
                                        val googleIdOption = GetGoogleIdOption.Builder()
                                            .setFilterByAuthorizedAccounts(false)
                                            .setServerClientId("831857688316-bvknta1vgnajr6vf9di3pol97bee44tl.apps.googleusercontent.com")
                                            .build()

                                        val request = GetCredentialRequest.Builder()
                                            .addCredentialOption(googleIdOption)
                                            .build()

                                        try {
                                            val result = credentialManager.getCredential(
                                                context = this@MainActivity,
                                                request = request
                                            )

                                            val credential = result.credential
                                            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                                val idToken = googleIdTokenCredential.idToken
                                                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                                                auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        userEmail = auth.currentUser?.email ?: "Success"
                                                    } else {
                                                        Log.e("Auth", "Firebase auth failed", task.exception)
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            Log.e("Auth", "Credential Manager failed", e)
                                        }
                                    }
                                }) {
                                    Text(text = "Sign in with Google")
                                }
                            } else {
                                Text(text = "Hello, $userEmail")
                            }
                        }
                    }
                }
            }
        }
    }
}
