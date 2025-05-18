package com.digiworld.digicount.staff

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.digiworld.digicount.system.SignInState
import com.digiworld.digicount.system.SignInViewModel
import com.digiworld.digicount.util.rememberImagePickerForState


@Composable
fun WelcomeInitScreen(onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App logo (you'll need to add this resource)
        // If R.drawable.app_logo doesn't exist, replace with any other icon resource
        // or comment out this Image composable
        /*
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "DigiCount Logo",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 24.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
        )
        */

        // App name
        Text(
            text = "Welcome to DigiCount",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // App description
        Text(
            text = "Field Work Monitoring Application",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Google Sign In Button
        Button(
            onClick = onSignInClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // You can add Google icon here if you have it as a resource
            Text(text = "Sign in with Google")
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Version info
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    onSignInClick: () -> Unit
) {
    val state by viewModel.signInState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is SignInState.Loading -> CircularProgressIndicator()
            is SignInState.Error -> Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Error: ${(state as SignInState.Error).error}",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onSignInClick) {
                    Text("Try Again")
                }
            }
            else -> Button(onClick = onSignInClick) {
                Text("Sign in with Google")
            }
        }
    }
}

@Composable
fun WelcomeScreen(userName: String, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome, $userName!",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You've successfully signed in with Google.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onNext) {
            Text("Next")
        }
    }
}

@Composable
fun UserInfoScreen(onNext: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Personal Information",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Next")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentUploadScreen(onNext: () -> Unit, onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Document Upload") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Document Upload",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                "Please upload the following required documents:",
                style = MaterialTheme.typography.bodyMedium
            )

            // Document upload fields based on requirements
            val nicImage = remember { mutableStateOf<Uri?>(null) }
            val birthCertImage = remember { mutableStateOf<Uri?>(null) }
            val drivingLicenseImage = remember { mutableStateOf<Uri?>(null) }
            val bikeInsuranceImage = remember { mutableStateOf<Uri?>(null) }
            val bikeRevenueLicenseImage = remember { mutableStateOf<Uri?>(null) }
            val gsCertificateImage = remember { mutableStateOf<Uri?>(null) }
            val policeCertificateImage = remember { mutableStateOf<Uri?>(null) }

            // Create launchers for each document type
            val nicLauncher = rememberImagePickerForState(nicImage)
            val birthCertLauncher = rememberImagePickerForState(birthCertImage)
            val drivingLicenseLauncher = rememberImagePickerForState(drivingLicenseImage)
            val bikeInsuranceLauncher = rememberImagePickerForState(bikeInsuranceImage)
            val bikeRevenueLicenseLauncher = rememberImagePickerForState(bikeRevenueLicenseImage)
            val gsCertificateLauncher = rememberImagePickerForState(gsCertificateImage)
            val policeCertificateLauncher = rememberImagePickerForState(policeCertificateImage)

            // Required documents (with * indicator)
            UploadField(
                label = "NIC Image *",
                uriState = nicImage,
                onClick = { nicLauncher.launch("image/*") },
                required = true
            )

            UploadField(
                label = "Birth Certificate *",
                uriState = birthCertImage,
                onClick = { birthCertLauncher.launch("image/*") },
                required = true
            )

            UploadField(
                label = "Driving License *",
                uriState = drivingLicenseImage,
                onClick = { drivingLicenseLauncher.launch("image/*") },
                required = true
            )

            UploadField(
                label = "Bike Insurance",
                uriState = bikeInsuranceImage,
                onClick = { bikeInsuranceLauncher.launch("image/*") }
            )

            UploadField(
                label = "Bike Revenue License",
                uriState = bikeRevenueLicenseImage,
                onClick = { bikeRevenueLicenseLauncher.launch("image/*") }
            )

            UploadField(
                label = "GS Certificate",
                uriState = gsCertificateImage,
                onClick = { gsCertificateLauncher.launch("image/*") }
            )

            UploadField(
                label = "Police Certificate",
                uriState = policeCertificateImage,
                onClick = { policeCertificateLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Validation logic - check if all required documents are uploaded
            val allRequiredDocsUploaded = nicImage.value != null &&
                    birthCertImage.value != null &&
                    drivingLicenseImage.value != null

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Back")
                }

                Button(
                    onClick = onNext,
                    enabled = allRequiredDocsUploaded
                ) {
                    Text("Next")
                }
            }

            if (!allRequiredDocsUploaded) {
                Text(
                    "Please upload all required documents marked with *",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun UploadField(
    label: String,
    uriState: MutableState<Uri?>,
    onClick: () -> Unit,
    required: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onClick) {
                Text("Upload")
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (uriState.value != null) {
                // Show image thumbnail
                Box(
                    modifier = Modifier.size(80.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(uriState.value),
                        contentDescription = label,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // View larger image button
                    IconButton(
                        onClick = { /* Navigate to full image screen with this URI */ },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "View full image",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Text(
                    "Uploaded",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            } else {
                Text(
                    if (required) "Required" else "Optional",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (required) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun ReviewScreen(onEdit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Review Your Information",
            style = MaterialTheme.typography.headlineMedium
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Display collected info here - in a real app, we'd pass this via ViewModel
        Text("Name: John Doe")
        Text("Email: john.doe@example.com")
        Text("Phone: +94 123 456 7890")

        Text(
            "Documents",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Text("NIC: Uploaded")
        Text("Birth Certificate: Uploaded")
        Text("Driving License: Uploaded")

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            OutlinedButton(onClick = onEdit) {
                Text("Edit")
            }

            Button(onClick = { /* Submit logic */ }) {
                Text("Submit")
            }
        }
    }
}

@Composable
fun FullImageScreen(uri: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        uri?.let {
            Image(
                painter = rememberAsyncImagePainter(Uri.parse(it)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        } ?: Text("No image available")
    }
}