package com.rifsxd.ksunext.ui.screen

import com.rifsxd.ksunext.ui.util.KernelUpdater
import com.ramcosta.composedestinations.generated.destinations.FlashScreenDestination
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.material3.LinearProgressIndicator
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
    val amber = Color(0xFFFFB454)
    var on by remember { mutableStateOf(RootXSafeMode.requested(context)) }
    var waiting by remember { mutableStateOf(RootXSafeMode.pending(context)) }
    var showReboot by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) { RootXSafeMode.applyPending(context) }
        waiting = RootXSafeMode.pending(context)
    }

    Row(
        Modifier
            .fillMaxWidth()
            .glass(RoundedCornerShape(24.dp))
            .border(1.5.dp, amber.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("Safe mode", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                if (waiting) "Waiting for root. It applies as soon as root is available."
                else if (on) "All modules are off. Turn this off to bring them back."
                else "Switch every module off in one tap",
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
    RebootDialog(showReboot) { showReboot = false }
}

@Composable
internal fun KernelUpdateSection(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val current = remember { KernelUpdater.currentRelease() }
    var latest by remember { mutableStateOf<KernelUpdater.Release?>(null) }
    var status by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    fun update(rel: KernelUpdater.Release) {
        scope.launch {
            busy = true
            error = null
            progress = 0f
            try {
                status = "Downloading ${rel.zipName} ..."
                val file = KernelUpdater.download(context, rel) { done, total ->
                    progress = if (total > 0) done.toFloat() / total.toFloat() else 0f
                }
                status = "Verifying checksum ..."
                val sha = KernelUpdater.sha256(file)
                val expected = rel.sha256
                    ?: throw IllegalStateException("This release has no published SHA-256 checksum. Not flashing.")
                if (!expected.equals(sha, ignoreCase = true)) {
                    throw IllegalStateException("Checksum mismatch, the download is corrupted. Not flashing.")
                }
                status = "Checking that this kernel trusts RootX ..."
                val entry = KernelUpdater.ownSignatureEntry(context)
                    ?: throw IllegalStateException("Could not read RootX's own signature. Not flashing.")
                val trusted = KernelUpdater.zipTrustsManager(file, entry)
                if (trusted == null) {
                    throw IllegalStateException("No kernel Image found in the zip, so it cannot be verified. Not flashing.")
                }
                if (!trusted) {
                    throw IllegalStateException("This kernel does NOT trust RootX's signature. Flashing it would lock RootX out of root. Not flashing.")
                }
                status = "Backing up the current boot.img ..."
                val backup = KernelUpdater.backupBoot().getOrThrow()
                status = "Backup saved: $backup"
                navigator.navigate(FlashScreenDestination(FlashIt.FlashAnyKernel(android.net.Uri.fromFile(file))))
            } catch (e: Exception) {
                error = e.message ?: e.toString()
            } finally {
                busy = false
            }
        }
    }

    ToolCard("Kernel update", "Download, verify and flash a new kernel from inside RootX") {
        Text(
            "Running kernel: $current",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(
            enabled = !busy,
            onClick = {
                scope.launch {
                    busy = true
                    error = null
                    status = "Checking for updates ..."
                    val r = KernelUpdater.fetchLatest()
                    r.onSuccess {
                        latest = it
                        status = if (KernelUpdater.tagBuild(it.tag) > KernelUpdater.currentBuild())
                            "Update available: ${it.tag}" else "You are on the latest kernel (${it.tag})"
                    }
                    r.onFailure { error = it.message ?: it.toString() }
                    busy = false
                }
            }
        ) { Text("Check for update") }

        latest?.let { rel ->
            if (rel.name.isNotBlank()) {
                Text(rel.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            }
            if (rel.notes.isNotBlank()) {
                Text(rel.notes.take(500), style = MaterialTheme.typography.bodySmall)
            }
            if (rel.sha256 == null) {
                Text(
                    "This release has no published checksum, so it cannot be installed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            val newer = KernelUpdater.tagBuild(rel.tag) > KernelUpdater.currentBuild()
            if (newer) {
                Button(enabled = !busy, onClick = { update(rel) }) { Text("Update kernel") }
            } else {
                OutlinedButton(enabled = !busy, onClick = { update(rel) }) { Text("Reinstall this kernel") }
            }
        }

        if (busy && progress > 0f) {
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        }
        if (status.isNotEmpty()) {
            Text(status, style = MaterialTheme.typography.bodySmall)
        }
        error?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
        }
        Text(
            "Safety: a boot.img backup is saved to /sdcard/RootX first, and a kernel that does not trust RootX is never flashed. " +
                "Keep your Odin stock firmware ready in case a kernel fails to boot.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
