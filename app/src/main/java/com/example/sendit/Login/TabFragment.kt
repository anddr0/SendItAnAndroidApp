import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.sendit.Adapters.LoginPagerAdapter
import com.example.sendit.HelperFunctions
import com.example.sendit.Login.LoginActivity
import com.example.sendit.R
import com.example.sendit.Repos.SupabaseRepo
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.example.sendit.NavMainActivity


class TabFragment : Fragment() {

    lateinit var gso: GoogleSignInOptions
    lateinit var gsc: GoogleSignInClient
    private lateinit var loginAdapter: LoginPagerAdapter
    private val sbRepo = SupabaseRepo()
    private val helpersFuncs = HelperFunctions()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab, container, false)

        loginAdapter = LoginPagerAdapter(this)
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        viewPager.adapter = loginAdapter

        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = if (position == 0) "SignIn" else "SignUp"
        }.attach()

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        gsc = GoogleSignIn.getClient(requireContext(), gso)

        val sign_btn: LinearLayout = view.findViewById(R.id.sign_layout)
        sign_btn.setOnClickListener {
            signIn()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val g_acc: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(requireContext())
        if (g_acc != null || requireContext().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE).getBoolean("SignedIn", false)) {
            navigateToSecondActivity()
        }
    }

    fun signIn() {
        val sing_in_intent: Intent = gsc.signInIntent
        startActivityForResult(sing_in_intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                task.getResult(ApiException::class.java)
                val g_acc: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(requireContext())
                if (g_acc != null) {
                    val personEmail: String = g_acc.email ?: ""
                    sbRepo.ifUserExists(lifecycleScope, personEmail) { exists ->
                        if (exists) {
                            sbRepo.getIdByEmail(lifecycleScope, personEmail) {idU ->
                                helpersFuncs.saveUserLogging(requireView(), idU)
                                navigateToSecondActivity()
                            }
                        }
                        else {
                            (activity as? LoginActivity)?.runToAnotherFragment(personEmail, helpersFuncs.generatePassword(12))
                        }
                    }
                }
            } catch (e: ApiException) {
//                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun navigateToSecondActivity() {
        requireActivity().finish()
        val intent = Intent(requireContext(), NavMainActivity::class.java)
        startActivity(intent)

//        val navController = findNavController()
//        navController.navigate(R.id.action_global_drawerMenuActivity)
//        requireActivity().finish()
    }
}
