package com.main.cipta_muri_mobile.ui.login

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.main.cipta_muri_mobile.R
import com.main.cipta_muri_mobile.data.SecureCredentialStore
import com.main.cipta_muri_mobile.databinding.ActivityLoginBinding
import com.main.cipta_muri_mobile.ui.main.MainActivity
import com.main.cipta_muri_mobile.ui.registrasi.RegistrasiActivity

class LoginActivity : AppCompatActivity() {

    // 1. DECLARE the binding object and viewModel
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()
    private var isFormattingDate = false
    private lateinit var credentialStore: SecureCredentialStore
    private val keyguardLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> fillCredentialsFromStore()
            Activity.RESULT_CANCELED -> Toast.makeText(
                this,
                getString(R.string.login_autofill_cancelled),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Jangan auto-forward ke Main; selalu tampilkan form login.

        // 2. INITIALIZE the binding object and set content view
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        credentialStore = SecureCredentialStore(applicationContext)

        setupDateInputFormat()
        setupListeners()
        setupAutofillFeature(savedInstanceState == null)
    }

    private fun setupDateInputFormat() {
        binding.etPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormattingDate) return

                val digits = s?.filter { it.isDigit() }?.take(8) ?: ""
                val formatted = StringBuilder()

                digits.forEachIndexed { index, c ->
                    formatted.append(c)
                    val shouldInsertDaySeparator = index == 1 && digits.length > 2
                    val shouldInsertMonthSeparator = index == 3 && digits.length > 4
                    if (shouldInsertDaySeparator || shouldInsertMonthSeparator) {
                        formatted.append('-')
                    }
                }

                val displayText = formatted.toString()

                isFormattingDate = true
                binding.etPin.setText(displayText)
                binding.etPin.setSelection(displayText.length)
                isFormattingDate = false
            }
        })
    }

    private fun setupListeners() {
        binding.btnMasuk.setOnClickListener {
            val nik = binding.etNik.text.toString().trim()
            val pinTanggalLahir = binding.etPin.text.toString().trim()

            if (nik.isEmpty() || pinTanggalLahir.isEmpty()) {
                Toast.makeText(this, "NIK dan Tanggal Lahir wajib diisi.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.etPin.error = null

            // Prefer API v2 (Bearer token) using tanggal_lahir as YYYY-MM-DD
            val normalizedTtl = normalizeDate(pinTanggalLahir)
            viewModel.loginV2(nik, normalizedTtl) { ok, msg ->
                if (ok) {
                    credentialStore.remember(nik, pinTanggalLahir)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    val message = msg ?: "Login gagal"
                    binding.etPin.error = message
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.tvLupaPin.setOnClickListener {
            startActivity(Intent(this, RegistrasiActivity::class.java))
        }
    }

    private fun normalizeDate(input: String): String {
        // Accept already-correct format
        val trimmed = input.trim()
        // Remove unexpected trailing chars (e.g., accidental 'S')
        val sanitized = trimmed.filter { it.isDigit() || it == '-' || it == '/' || it == '.' }
        val backendRegex = Regex("^\\d{4}-\\d{2}-\\d{2}$")
        if (backendRegex.matches(sanitized)) return sanitized

        val displayRegex = Regex("^\\d{2}-\\d{2}-\\d{4}$")
        if (displayRegex.matches(sanitized)) {
            val parts = sanitized.split("-")
            return "${parts[2]}-${parts[1]}-${parts[0]}"
        }

        // Try dd-MM-yyyy or dd/MM/yyyy or ddMMyyyy
        val digits = sanitized.replace("/", "-").replace(".", "-")
        val parts = digits.split("-")
        if (parts.size == 3 && parts[0].length == 2 && parts[1].length == 2 && parts[2].length == 4) {
            return "${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}"
        }
        if (sanitized.length == 8 && sanitized.all { it.isDigit() }) {
            val dd = sanitized.substring(0, 2)
            val mm = sanitized.substring(2, 4)
            val yyyy = sanitized.substring(4, 8)
            return "$yyyy-$mm-$dd"
        }
        // Fallback: return as-is; backend will validate
        return sanitized
    }

    private fun setupAutofillFeature(shouldAutoPrompt: Boolean) {
        val hasSaved = credentialStore.hasCredentials()
        binding.btnAutofill.isVisible = hasSaved
        binding.btnAutofill.setOnClickListener { requestAutofillWithDeviceAuth() }

        if (shouldAutoPrompt && hasSaved) {
            binding.btnAutofill.post { requestAutofillWithDeviceAuth() }
        }
    }

    private fun requestAutofillWithDeviceAuth() {
        if (!credentialStore.hasCredentials()) {
            binding.btnAutofill.isVisible = false
            Toast.makeText(this, getString(R.string.login_autofill_no_data), Toast.LENGTH_SHORT).show()
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            launchDeviceCredentialConfirmation()
            return
        }

        val biometricManager = BiometricManager.from(this)
        val canAuthenticate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else {
            biometricManager.canAuthenticate()
        }

        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            showBiometricPrompt()
        } else {
            launchDeviceCredentialConfirmation()
        }
    }

    private fun showBiometricPrompt() {
        val prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                fillCredentialsFromStore()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> return
                    BiometricPrompt.ERROR_LOCKOUT,
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT,
                    BiometricPrompt.ERROR_HW_UNAVAILABLE,
                    BiometricPrompt.ERROR_HW_NOT_PRESENT,
                    BiometricPrompt.ERROR_NO_BIOMETRICS -> launchDeviceCredentialConfirmation()
                    else -> Toast.makeText(this@LoginActivity, errString, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(
                    this@LoginActivity,
                    getString(R.string.login_autofill_cancelled),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        val builder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.login_autofill_prompt_title))
            .setDescription(getString(R.string.login_autofill_prompt_subtitle))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            builder.setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setDeviceCredentialAllowed(true)
        }

        prompt.authenticate(builder.build())
    }

    private fun launchDeviceCredentialConfirmation() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (keyguardManager?.isDeviceSecure != true) {
            fillCredentialsFromStore()
            return
        }

        val intent = keyguardManager.createConfirmDeviceCredentialIntent(
            getString(R.string.login_autofill_prompt_title),
            getString(R.string.login_autofill_keyguard_description)
        )

        if (intent != null) {
            keyguardLauncher.launch(intent)
        } else {
            fillCredentialsFromStore()
        }
    }

    private fun fillCredentialsFromStore(showToast: Boolean = true) {
        val savedNik = credentialStore.getNik() ?: ""
        val savedBirthDate = credentialStore.getBirthDate() ?: ""

        if (savedNik.isNotEmpty()) {
            binding.etNik.setText(savedNik)
        }
        if (savedBirthDate.isNotEmpty()) {
            isFormattingDate = true
            binding.etPin.setText(savedBirthDate)
            binding.etPin.setSelection(binding.etPin.text?.length ?: 0)
            isFormattingDate = false
        }

        if (showToast && (savedNik.isNotEmpty() || savedBirthDate.isNotEmpty())) {
            Toast.makeText(this, getString(R.string.login_autofill_success), Toast.LENGTH_SHORT).show()
        }
    }
}
