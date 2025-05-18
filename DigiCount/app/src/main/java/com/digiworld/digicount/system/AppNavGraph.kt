package com.digiworld.digicount.system

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.digiworld.digicount.staff.DocumentUploadScreen
import com.digiworld.digicount.staff.FullImageScreen
import com.digiworld.digicount.staff.ReviewScreen
import com.digiworld.digicount.staff.SignInScreen
import com.digiworld.digicount.staff.UserInfoScreen
import com.digiworld.digicount.staff.WelcomeInitScreen
import com.digiworld.digicount.staff.WelcomeScreen


@Composable
fun AppNavGraph(
    viewModel: SignInViewModel,
    onGoogleSignIn: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "welcome_init") {
        composable("welcome_init") {
            WelcomeInitScreen(
                onSignInClick = { navController.navigate("signin") },
            )
        }

        composable("signin") {
            // Observe state and navigate on success
            val state = viewModel.signInState.collectAsState().value
            LaunchedEffect(state) {
                if (state is SignInState.Success) {
                    navController.navigate("welcome_user/${state.userName}") {
                        popUpTo("welcome_init") { inclusive = true }
                    }
                }
            }

            SignInScreen(
                viewModel = viewModel,
                onSignInClick = onGoogleSignIn
            )
        }

        composable(
            "welcome_user/{userName}",
            arguments = listOf(navArgument("userName") {
                type = NavType.StringType
                nullable = true
            })
        ) { entry ->
            val userName = entry.arguments?.getString("userName") ?: "User"
            WelcomeScreen(
                userName = userName,
                onNext = { navController.navigate("user_info") }
            )
        }

        composable("user_info") {
            UserInfoScreen(onNext = { navController.navigate("document_upload") })
        }

        composable("document_upload") {
            DocumentUploadScreen(
                onNext = { navController.navigate("review") },
                onBackClick = { navController.popBackStack("user_info", false) }
            )
        }

        composable("review") {
            ReviewScreen(onEdit = { navController.popBackStack("user_info", false) })
        }

        composable(
            "full_image",
            arguments = listOf(navArgument("uri") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { entry ->
            FullImageScreen(uri = entry.arguments?.getString("uri"))
        }
    }
}