package ml.a0x00000000.mjavascript

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.*
import android.support.annotation.StringRes
import android.text.TextUtils
import android.view.MenuItem

/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || EnginesPreferenceFragment::class.java.name == fragmentName
                || HelpFeedbackPreferenceFragment::class.java.name == fragmentName
                || AboutReadmePreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows engines preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class EnginesPreferenceFragment : PreferenceFragment() {
        lateinit var prefDefaultFilename: EditTextPreference
        lateinit var prefDefaultEngine: ListPreference
        lateinit var prefNeverAsk: SwitchPreference
        lateinit var prefProjectPath: EditTextPreference
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_engines)
            setHasOptionsMenu(true)

            prefDefaultFilename = findPreference("default_filename") as EditTextPreference
            prefDefaultEngine = findPreference("default_engine") as ListPreference
            prefNeverAsk = findPreference("never_ask") as SwitchPreference
            prefProjectPath = findPreference("project_path") as EditTextPreference

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(prefDefaultFilename)
            bindPreferenceSummaryToValue(prefDefaultEngine)
            bindPreferenceSummaryToValue(prefProjectPath)


            var defaultListener = prefDefaultFilename.onPreferenceChangeListener
            prefDefaultFilename.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { pref, value ->
                if((value as String).isBlank()) {
                    Settings.setDefaultFilenameFromSettingsActivity(value)
                    return@OnPreferenceChangeListener defaultListener.onPreferenceChange(pref, getString(R.string.default_filename))
                } else {
                    Settings.setDefaultFilenameFromSettingsActivity(value)
                    return@OnPreferenceChangeListener defaultListener.onPreferenceChange(pref, value)
                }
            }

            defaultListener = prefNeverAsk.onPreferenceChangeListener
            prefNeverAsk.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { pref, value ->
                Settings.neverAsk = value as Boolean
                val ret: Boolean = defaultListener.onPreferenceChange(pref, value)
                pref.setSummary(R.string.pref_description_always_use_default_engine)
                return@OnPreferenceChangeListener ret
            }

            defaultListener = prefDefaultEngine.onPreferenceChangeListener
            prefDefaultEngine.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { pref, value ->
                for(engine in Engines.values()) {
                    if(engine.getEngineName() == value) Settings.defaultEngine = engine
                }
                return@OnPreferenceChangeListener defaultListener.onPreferenceChange(pref, value)
            }

            prefProjectPath.text = Settings.projectPath
            prefProjectPath.setDefaultValue(Settings.projectPath)
            prefProjectPath.summary = Settings.projectPath
            defaultListener = prefProjectPath.onPreferenceChangeListener
            prefProjectPath.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { pref, value ->
                if((value as String).isBlank()) {
                    Settings.projectPath = Environment.getExternalStorageDirectory().path
                } else {
                    Settings.setProjectPathFromSettingsActivity(value)
                }
                return@OnPreferenceChangeListener defaultListener.onPreferenceChange(pref, Settings.projectPath)
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class AboutReadmePreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_about_readme)
            setHasOptionsMenu(true)

            findPreference("github").setOnPreferenceClickListener { _ ->
                openLink(activity, R.string.pref_description_github)
                return@setOnPreferenceClickListener false
            }

            findPreference("readme").setOnPreferenceClickListener { _ ->
                openLink(activity, R.string.pref_description_readme)
                return@setOnPreferenceClickListener false
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class HelpFeedbackPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_help_feedback)
            setHasOptionsMenu(true)

            findPreference("github").setOnPreferenceClickListener { _ ->
                openLink(activity, R.string.pref_description_github_issue)
                return@setOnPreferenceClickListener false
            }

            findPreference("email").setOnPreferenceClickListener { _ ->
                val email = arrayOf(getString(R.string.pref_description_email))
//                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(email[0]))
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "message/rfc822"
                intent.putExtra(Intent.EXTRA_EMAIL, email)
////                intent.putExtra(Intent.EXTRA_CC, email)
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.sample_email_subject))
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sample_email_text))
                startActivity(Intent.createChooser(intent, getString(R.string.choose_email_client)))

                return@setOnPreferenceClickListener false
            }
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    companion object {

        private fun openLink(context: Context, @StringRes linkId: Int) {
            openLink(context, context.getString(linkId))
        }

        private fun openLink(context: Context, link: String) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        }

        /**
         * A preference value change listener that updates the preference's summary
         * to reflect its new value.
         */
        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                val index = preference.findIndexOfValue(stringValue)

                // Set the summary to reflect the new value.
                preference.setSummary(
                        if (index >= 0)
                            preference.entries[index]
                        else
                            null)

            } else if (preference is RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
//                    preference.setSummary(R.string.pref_ringtone_silent)

                } else {
                    val ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue))

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null)
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        val name = ringtone.getTitle(preference.getContext())
                        preference.setSummary(name)
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.summary = stringValue
            }
            true
        }

        /**
         * Helper method to determine if the device has an extra-large screen. For
         * example, 10" tablets are extra-large.
         */
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the
         * preference's value is changed, its summary (line of text below the
         * preference title) is updated to reflect the value. The summary is also
         * immediately updated upon calling this method. The exact display format is
         * dependent on the type of preference.

         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValue(preference: Preference) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
    }
}
