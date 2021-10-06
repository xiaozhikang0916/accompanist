/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.accompanist.sample.permissions

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.sample.AccompanistSampleTheme

@OptIn(ExperimentalPermissionsApi::class)
class RequestPermissionSample : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AccompanistSampleTheme {
                val cameraPermissionState =
                    rememberPermissionState(android.Manifest.permission.CAMERA)

                // Track if the permission has been already requested to the user
                // This should be saved in the data layer
                var permissionRequested by rememberSaveable { mutableStateOf(false) }

                Sample(
                    cameraPermission = cameraPermissionState,
                    isPermissionRequested = permissionRequested,
                    onPermissionRequest = { permissionRequested = true }
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Sample(
    cameraPermission: PermissionState,
    isPermissionRequested: Boolean,
    onPermissionRequest: () -> Unit
) {
    // Track if the user doesn't want to see the rationale any more.
    var doNotShowRationale by rememberSaveable { mutableStateOf(false) }

    PermissionRequired(
        permissionState = cameraPermission,
        isPermissionRequested = isPermissionRequested,
        requestPermissionContent = {
            if (doNotShowRationale) {
                Text("Feature not available")
            } else {
                Rationale(
                    onDoNotShowRationale = { doNotShowRationale = true },
                    onRequestPermission = {
                        cameraPermission.launchPermissionRequest()
                        onPermissionRequest()
                    }
                )
            }
        },
        permissionNotAvailableContent = {
            PermissionNotAvailable()
        }
    ) {
        Text("Camera permission Granted")
    }
}

@Composable
private fun Rationale(
    onDoNotShowRationale: () -> Unit,
    onRequestPermission: () -> Unit
) {
    Column {
        Text("The camera is important for this app. Please grant the permission.")
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Button(onClick = onRequestPermission) {
                Text("Request permission")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = onDoNotShowRationale) {
                Text("Don't show rationale again")
            }
        }
    }
}

@Composable
private fun PermissionNotAvailable() {
    Column {
        Text("Camera permission not available!")
    }
}
