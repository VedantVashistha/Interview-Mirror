package com.interviewmirror.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.interviewmirror.app.ui.InterviewMirrorRoot
import com.interviewmirror.app.ui.theme.InterviewMirrorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InterviewMirrorTheme {
                InterviewMirrorRoot()
            }
        }
    }
}
