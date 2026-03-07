package com.adriantache.projecttracker.ui.view

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.adriantache.projecttracker.ui.theme.AccentTeal
import com.adriantache.projecttracker.ui.theme.BackgroundDark
import com.adriantache.projecttracker.ui.theme.InterFamily
import com.adriantache.projecttracker.ui.theme.PlayfairFamily
import com.adriantache.projecttracker.ui.theme.TextCream
import com.adriantache.projecttracker.ui.theme.TextMuted
import com.adriantache.projecttracker.ui.viewModel.AuthViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun AuthView(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val currentUser by viewModel.currentUser.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        if (currentUser == null) {
            AuthContent(auth = viewModel.auth)
        } else {
            content()
        }
    }
}

@Composable
private fun AuthContent(
    modifier: Modifier = Modifier,
    auth: FirebaseAuth,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = AccentTeal,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to Project Tracker",
            fontFamily = PlayfairFamily,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = TextCream,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Please sign in to continue",
            fontFamily = InterFamily,
            fontSize = 18.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            CircularProgressIndicator(color = AccentTeal)
        } else {
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    scope.launch {
                        try {
                            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                                serverClientId = "831857688316-bvknta1vgnajr6vf9di3pol97bee44tl.apps.googleusercontent.com"
                            ).build()

                            val request = GetCredentialRequest.Builder()
                                .addCredentialOption(signInWithGoogleOption)
                                .build()

                            val result = credentialManager.getCredential(
                                context = context,
                                request = request
                            )

                            val credential = result.credential
                            if (credential is CustomCredential &&
                                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                            ) {

                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                val idToken = googleIdTokenCredential.idToken
                                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                                auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        errorMessage = task.exception?.message ?: "Firebase authentication failed"
                                        isLoading = false
                                    }
                                    // Success is handled by AuthViewModel's AuthStateListener
                                }
                            } else {
                                errorMessage = "Unexpected credential type received"
                                isLoading = false
                            }
                        } catch (e: Exception) {
                            Log.e("Auth", "Credential Manager failed", e)
                            errorMessage = e.message ?: "Failed to get credentials"
                            isLoading = false
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentTeal,
                    contentColor = BackgroundDark
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 32.dp,
                    vertical = 16.dp
                )
            ) {
                Text(
                    "SIGN IN WITH GOOGLE",
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
