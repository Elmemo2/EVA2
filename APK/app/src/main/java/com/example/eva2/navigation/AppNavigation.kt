package com.example.eva2.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eva2.ui.screens.HistoryScreen
import com.example.eva2.ui.screens.LoginScreen
import com.example.eva2.ui.screens.MonitoringScreen
import com.example.eva2.ui.screens.RegisterScreen
import com.example.eva2.ui.viewmodel.AuthViewModel
import com.example.eva2.ui.viewmodel.HistoryViewModel
import com.example.eva2.ui.viewmodel.MonitoringViewModel

object Screen {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MONITORING = "monitoring"
    const val HISTORY = "history"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    monitoringViewModel: MonitoringViewModel = viewModel(),
    historyViewModel: HistoryViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.LOGIN
    ) {
        composable(Screen.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.MONITORING) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateBackToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    authViewModel.resetState()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.MONITORING) {
            MonitoringScreen(
                viewModel = monitoringViewModel,
                onNavigateToHistory = {
                    navController.navigate(Screen.HISTORY)
                },
                onSignOut = {
                    authViewModel.resetState()
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(Screen.MONITORING) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.HISTORY) {
            HistoryScreen(
                viewModel = historyViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
