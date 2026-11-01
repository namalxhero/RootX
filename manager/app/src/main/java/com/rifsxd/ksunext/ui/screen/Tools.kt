package com.rifsxd.ksunext.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.rifsxd.ksunext.Natives
import com.rifsxd.ksunext.ui.component.XBoom
import com.rifsxd.ksunext.ui.component.XBoomMood
import com.rifsxd.ksunext.ui.component.glass
import com.rifsxd.ksunext.ui.util.CheckResult
import com.rifsxd.ksunext.ui.util.RootTimer
import com.rifsxd.ksunext.ui.util.RootPreset
import com.rifsxd.ksunext.ui.util.RootXPresets
import com.rifsxd.ksunext.ui.util.RootXSafeMode
import com.rifsxd.ksunext.ui.util.SelfCheck
import com.rifsxd.ksunext.ui.util.reboot
import com.rifsxd.ksunext.ui.util.toggleModule
import com.rifsxd.ksunext.ui.viewmodel.ModuleViewModel
import com.rifsxd.ksunext.ui.viewmodel.SuperUserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
private fun ToolCard(title: String, subtitle: String, accent: Color? = null, content: @Composable ColumnScope.() -> Unit) {
    var open by rememberSaveable(title) { mutableStateOf(false) }
    Column(
        Modifier.fillMaxWidth().glass(RoundedCornerShape(28.dp))
            .then(if (accent != null) Modifier.border(1.5.dp, accent.copy(alpha = 0.7f), RoundedCornerShape(24.dp)) else Modifier)
    ) {
        Row(
            Modifier.fillMaxWidth().clickable { open = !open }.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(if (open) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null)
        }
        AnimatedVisibility(open) {
            Column(
                Modifier.padding(horizontal = 18.dp).padding(bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = content
            )
        }
    }
}

@Composable
private fun RebootDialog(show: Boolean, onDismiss: () -> Unit) {
    if (!show) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reboot needed") },
        text = { Text("Module changes apply after a reboot. Reboot now?") },
        confirmButton = { TextButton(onClick = { onDismiss(); reboot() }) { Text("Reboot") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Later") } }
    )
}

// ---------------------------------------------------------------------------

@Composable
internal fun SelfCheckSection() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var results by remember { mutableStateOf<List<CheckResult>>(emptyList()) }
    var running by remember { mutableStateOf(false) }
    ToolCard("Self check", "Looks for root traces that apps can detect") {
        Button(onClick = {
            scope.launch {
                running = true
                results = withContext(Dispatchers.IO) { SelfCheck.run(context) }
                running = false
            }
        }) { Text(if (running) "Checking..." else "Run self check") }
        if (results.isNotEmpty()) {
            Text(
                "Passed ${results.count { it.ok }} of ${results.size}",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            results.forEach { r ->
                Row(verticalAlignment = Alignment.Top) {
                    Text(if (r.ok) "OK" else "!!", color = if (r.ok) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
                    Column {
                        Text(r.title, style = MaterialTheme.typography.bodyMedium)
                        Text(r.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------

@Composable
private fun HourStepper(label: String, hour: Int, onChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, modifier = Modifier.width(52.dp))
        OutlinedButton(onClick = { onChange((hour + 23) % 24) }) { Text("-") }
        Text("%02d:00".format(hour), fontWeight = FontWeight.SemiBold)
        OutlinedButton(onClick = { onChange((hour + 1) % 24) }) { Text("+") }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TemporaryRootCard(packageName: String, appLabel: String, uid: Int) {
    val context = LocalContext.current
    val amber = Color(0xFFFFB454)
    var tick by remember { mutableIntStateOf(0) }
    var preset by remember { mutableStateOf(RootPreset.Full) }
    var startH by remember { mutableIntStateOf(22) }
    var endH by remember { mutableIntStateOf(6) }
    var message by remember { mutableStateOf("") }
    val temp = remember(tick) { RootTimer.listTemps(context).firstOrNull { it.pkg == packageName } }
    val win = remember(tick) { RootTimer.listWindows(context).firstOrNull { it.pkg == packageName } }

    Column(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .glass(RoundedCornerShape(28.dp))
            .border(1.5.dp, amber.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Temporary root", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text("Root that switches off by itself for $appLabel", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Profile", style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RootPreset.values().forEach { pr ->
                FilterChip(selected = preset == pr, onClick = { preset = pr }, label = { Text(pr.label) })
            }
        }
        Text("Apply it for", style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("10 min" to 10, "1 hour" to 60, "24 hours" to 1440, "Today" to RootTimer.minutesUntilMidnight()).forEach { (t, m) ->
                OutlinedButton(onClick = {
                    val ok = RootTimer.grantTemporary(context, packageName, uid, m, preset)
                    message = if (ok) "${preset.label} set for $t" else "Could not change the profile"
                    tick++
                }) { Text(t) }
            }
        }
        Text("Or only during these hours, every day", style = MaterialTheme.typography.labelLarge)
        HourStepper("From", startH) { startH = it }
        HourStepper("To", endH) { endH = it }
        Button(onClick = {
            RootTimer.addWindow(context, packageName, uid, startH, endH)
            message = "Daily window saved"
            tick++
        }) { Text("Save daily window") }
        if (message.isNotEmpty()) Text(message, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
        temp?.let { t ->
            val left = ((t.until - System.currentTimeMillis()) / 60000).coerceAtLeast(0)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Temporary root: $left min left", Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { RootTimer.revokeNow(context, packageName); tick++ }) { Text("Revoke") }
            }
        }
        win?.let { w ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("%02d:00 to %02d:00 daily".format(w.startHour, w.endHour), Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { RootTimer.removeWindow(context, packageName); tick++ }) { Text("Remove") }
            }
        }
    }
}

// ---------------------------------------------------------------------------

@Composable
internal fun PresetsSection() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val modVm: ModuleViewModel = viewModel()
    var presets by remember { mutableStateOf(RootXPresets.load(context)) }
    var name by remember { mutableStateOf("") }
    var showReboot by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { if (Natives.isManager) modVm.fetchModuleList() }

    ToolCard("Module presets", "Switch whole sets of modules in one tap") {
        if (!Natives.isManager) {
            Text("Needs RootX to be active first.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Preset name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Button(enabled = name.isNotBlank(), onClick = {
                RootXPresets.save(context, name.trim(), modVm.moduleList.filter { it.enabled }.map { it.id })
                presets = RootXPresets.load(context)
                name = ""
            }) { Text("Save enabled modules as preset") }
            presets.forEach { (pn, ids) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$pn (${ids.size})", Modifier.weight(1f))
                    TextButton(onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) { modVm.moduleList.forEach { toggleModule(it.id, it.id in ids) } }
                            showReboot = true
                        }
                    }) { Text("Apply") }
                    TextButton(onClick = { RootXPresets.delete(context, pn); presets = RootXPresets.load(context) }) { Text("Delete") }
                }
            }
        }
    }
    RebootDialog(showReboot) { showReboot = false }
}

// ---------------------------------------------------------------------------

@Composable
internal fun SafeModeSection() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var on by remember { mutableStateOf(RootXSafeMode.requested(context)) }
    var waiting by remember { mutableStateOf(RootXSafeMode.pending(context)) }
    var showReboot by remember { mutableStateOf(false) }

    ToolCard("Safe mode", "Switch every module off until you turn it back on", accent = Color(0xFFFFB454)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(if (on) "Safe mode is ON" else "Safe mode is off", fontWeight = FontWeight.SemiBold)
                Text(
                    if (waiting) "Waiting for root. It will apply as soon as root is available."
                    else if (on) "All modules stay off. Turn this off to bring them back."
                    else "Turning it on switches all modules off.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(colors = com.rifsxd.ksunext.ui.component.iosSwitchColors(), checked = on, onCheckedChange = { want ->
                on = want
                scope.launch {
                    val ok = withContext(Dispatchers.IO) { RootXSafeMode.set(context, want) }
                    waiting = !ok
                    if (ok) showReboot = true
                }
            })
        }
    }
    RebootDialog(showReboot) { showReboot = false }
}
