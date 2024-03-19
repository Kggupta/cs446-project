package com.example.clubwat.views

import HomeViewModel
import LoginViewModel
import SearchViewModel
import SignUpViewModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clubwat.repository.UserRepository
import com.example.clubwat.ui.theme.ClubWATTheme
import com.example.clubwat.viewmodels.AddEventViewModel
import com.example.clubwat.viewmodels.ClubDetailsViewModel
import com.example.clubwat.viewmodels.ClubManagementViewModel
import com.example.clubwat.viewmodels.ClubUserManagementViewModel
import com.example.clubwat.viewmodels.CodeVerificationViewModel
import com.example.clubwat.viewmodels.EditClubDetailsViewModel
import com.example.clubwat.viewmodels.EditEventDetailsViewModel
import com.example.clubwat.viewmodels.EventDetailsViewModel
import com.example.clubwat.viewmodels.ForYouViewModel
import com.example.clubwat.viewmodels.InboxViewModel
import com.example.clubwat.viewmodels.ProfileViewModel
import com.example.clubwat.viewmodels.factories.AddEventViewModelFactory
import com.example.clubwat.viewmodels.factories.ClubDetailsViewModelFactory
import com.example.clubwat.viewmodels.factories.ClubManagementViewModelFactory
import com.example.clubwat.viewmodels.factories.ClubUserManagementViewModelFactory
import com.example.clubwat.viewmodels.factories.CodeVerificationViewModelFactory
import com.example.clubwat.viewmodels.factories.EditClubDetailsViewModelFactory
import com.example.clubwat.viewmodels.factories.EditEventDetailsViewModelFactory
import com.example.clubwat.viewmodels.factories.EventDetailsViewModelFactory
import com.example.clubwat.viewmodels.factories.ForYouViewModelFactory
import com.example.clubwat.viewmodels.factories.HomeViewModelFactory
import com.example.clubwat.viewmodels.factories.InboxViewModelFactory
import com.example.clubwat.viewmodels.factories.LoginViewModelFactory
import com.example.clubwat.viewmodels.factories.ProfileViewModelFactory
import com.example.clubwat.viewmodels.factories.SearchViewModelFactory
import com.example.clubwat.viewmodels.factories.SignUpViewModelFactory
import com.example.clubwat.views.NavigationBar.NavBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

    override fun onResume() {
        super.onResume()
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClubWATTheme {
                val navController = rememberNavController()
                val currentUser by userRepository.currentUser

                // Todo -> 1. delete all factories 2. Add @Inject constructor() to each vm 3. add = hiltViewModel() to each views constructor (Discussion VM/View for example)
                val signUpViewModel: SignUpViewModel by viewModels { SignUpViewModelFactory(userRepository) }
                val loginViewModel: LoginViewModel by viewModels { LoginViewModelFactory(userRepository) }
                val codeVerificationViewModel: CodeVerificationViewModel by viewModels { CodeVerificationViewModelFactory(userRepository) }
                val homeViewModel: HomeViewModel by viewModels { HomeViewModelFactory(userRepository) }
                val forYouViewModel: ForYouViewModel by viewModels { ForYouViewModelFactory(userRepository) }
                val profileViewModel: ProfileViewModel by viewModels { ProfileViewModelFactory(userRepository) }
                val searchViewModel: SearchViewModel by viewModels { SearchViewModelFactory(userRepository) }
                val clubDetailsViewModel: ClubDetailsViewModel by viewModels { ClubDetailsViewModelFactory(userRepository) }
                val inboxViewModel: InboxViewModel by viewModels { InboxViewModelFactory(userRepository) }
                val eventDetailsViewModel: EventDetailsViewModel by viewModels { EventDetailsViewModelFactory(userRepository) }
                val clubManagementViewModel: ClubManagementViewModel by viewModels { ClubManagementViewModelFactory(userRepository) }
                val clubUserManagementViewModel: ClubUserManagementViewModel by viewModels { ClubUserManagementViewModelFactory(userRepository) }
                val addEventViewModel: AddEventViewModel by viewModels { AddEventViewModelFactory(userRepository) }
                val editClubDetailsViewModel: EditClubDetailsViewModel by viewModels { EditClubDetailsViewModelFactory(userRepository) }
                val editEventDetailsViewModel: EditEventDetailsViewModel by viewModels { EditEventDetailsViewModelFactory(userRepository) }

                Scaffold(
                    bottomBar = {
                        if (currentUser?.userId != null) {
                            NavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = Color.White
                    ) {
                        NavHost(navController = navController, startDestination = "login") {
                            composable("signup") {
                                SignUpView(viewModel = signUpViewModel, navController = navController)
                            }
                            composable("login") {
                                LoginView(viewModel = loginViewModel, navController = navController)
                            }
                            composable("verification") {
                                CodeVerificationView(viewModel = codeVerificationViewModel, navController = navController)
                            }
                            composable("home") {
                                HomeView(viewModel = homeViewModel, navController = navController)
                            }
                            composable("forYou") {
                                ForYouView(viewModel = forYouViewModel, navController = navController)
                            }
                            composable("profile") {
                                ProfileView(viewModel = profileViewModel, navController = navController)
                            }
                            composable("search") {
                                SearchView(viewModel = searchViewModel, navController = navController)
                            }
                            composable("club/{clubId}") { backStackEntry ->
                                ClubDetailsView(
                                    viewModel = clubDetailsViewModel,
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId")
                                )
                            }
                            composable("discussion/{clubId}") { backStackEntry ->
                                ClubDiscussionView(
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId")
                                )
                            }
                            composable("editprofile") {
                                EditProfileView(navController = navController)
                            }
                            composable("password") {
                                ChangePasswordView(navController = navController)
                            }
                            composable("friends") {
                                ManageFriendsView(navController = navController)
                            }
                            composable("interests") {
                                UserInterestsView(navController = navController)
                            }
                            composable("inbox") {
                                InboxView(viewModel = inboxViewModel, navController = navController)
                            }
                            composable("event/{eventId}") {backStackEntry ->
                                EventDetailsView(viewModel = eventDetailsViewModel,
                                    navController = navController,
                                    eventId = backStackEntry.arguments?.getString("eventId"))
                            }
                            composable("event/{eventId}/eventDetails") {backStackEntry ->
                                EditEventDetailsView(viewModel = editEventDetailsViewModel,
                                    navController = navController,
                                    eventId = backStackEntry.arguments?.getString("eventId"))
                            }
                            composable("club/{clubId}/event/new/{type}") {backStackEntry ->
                                AddEventView(viewModel = addEventViewModel,
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId"),
                                    type = backStackEntry.arguments?.getString("type"))
                            }
                            composable("club/{clubId}/management") {backStackEntry ->
                                ClubManagementView(viewModel = clubManagementViewModel,
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId"))
                            }
                            composable("club/{clubId}/management/users") {backStackEntry ->
                                ClubUserManagementView(viewModel = clubUserManagementViewModel,
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId"))
                            }
                            composable("club/{clubId}/management/clubDetails") {backStackEntry ->
                                EditClubDetailsView(viewModel = editClubDetailsViewModel,
                                    navController = navController,
                                    clubId = backStackEntry.arguments?.getString("clubId"))
                            }
                        }
                    }
                }
            }
        }
    }
}
