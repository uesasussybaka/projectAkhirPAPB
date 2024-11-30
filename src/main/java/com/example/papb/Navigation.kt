package com.example.papb

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.papb.pages.HomePage
import com.example.papb.pages.LoginPage
import com.example.papb.pages.SignInPage

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel = AuthViewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginPage(navController, authViewModel) }
        composable("signin") { SignInPage(navController, authViewModel) }
        composable("home") { HomePage() }
    }
}