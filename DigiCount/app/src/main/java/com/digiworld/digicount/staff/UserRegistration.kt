import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter

@Composable
fun UserRegistrationScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "user_info") {
        composable("user_info") { UserInfoScreen(navController) }
        composable("document_upload") { DocumentUploadScreen(navController) }
        composable("review") { ReviewScreen(navController) }
        composable("full_image?uri={uri}") { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")
            FullImageScreen(uri = uri)
        }
    }
}

@Composable
fun UserInfoScreen(navController: NavHostController) {
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
        TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
        TextField(value = phoneNumber, onValueChange = { phoneNumber = it }, label = { Text("Phone Number") })

        Button(onClick = { navController.navigate("document_upload") }) { Text("Next") }
    }
}

@Composable
fun DocumentUploadScreen(navController: NavHostController) {
    val nicImage = remember { mutableStateOf<Uri?>(null) }
    val birthCertificateImage = remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ImageUploadField("NIC Image", nicImage)
        ImageUploadField("Birth Certificate Image", birthCertificateImage)

        Button(onClick = { navController.navigate("review") }) { Text("Review") }
    }
}

@Composable
fun ImageUploadField(label: String, imageUri: MutableState<Uri?>) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri.value = uri
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 16.sp)
        Button(onClick = { launcher.launch("image/*") }) { Text("Upload $label") }
        imageUri.value?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = label,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ReviewScreen(navController: NavHostController) {
    val nicImage = remember { mutableStateOf<Uri?>(null) }
    val birthCertificateImage = remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Review Your Information", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text("Name: ")
        Text("Email: ")
        Text("Phone Number: ")

        Text("Uploaded Documents:")
        Text("NIC Image:")
        nicImage.value?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "NIC Image",
                modifier = Modifier
                    .size(100.dp)
                    .clickable { navController.navigate("full_image?uri=${it}") },
                contentScale = ContentScale.Crop
            )
        }

        Text("Birth Certificate Image:")
        birthCertificateImage.value?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Birth Certificate Image",
                modifier = Modifier
                    .size(100.dp)
                    .clickable { navController.navigate("full_image?uri=${it}") },
                contentScale = ContentScale.Crop
            )
        }

        Button(onClick = { /* Submit registration logic */ }) { Text("Submit") }
        Button(onClick = { navController.popBackStack() }) { Text("Edit") }
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
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Full Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}
