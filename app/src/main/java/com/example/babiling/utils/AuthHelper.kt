package com.example.babiling.utils

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

object AuthHelper {

    @Composable
    fun getGoogleClient(): GoogleSignInClient {
        val context = LocalContext.current

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(
                "683013257609-3i3u7lhl7bimaq1jihjqkohjs44cquft.apps.googleusercontent.com"
            )
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    /**
     * ✅ Launcher nhận credential Google
     */
    @Composable
    fun firebaseAuthLauncher(
        onSuccess: (AuthCredential) -> Unit,
        onError: (ApiException) -> Unit
    ): ManagedActivityResultLauncher<Intent, ActivityResult> {

        return rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                try {
                    val account = task.getResult(ApiException::class.java)!!
                    val credential =
                        GoogleAuthProvider.getCredential(account.idToken!!, null)

                    onSuccess(credential)

                } catch (e: ApiException) {
                    onError(e)
                }
            }
        }
    }
}
