package com.example.sangallae.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.LayoutInflater.from
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.sangallae.GlobalApplication
import com.example.sangallae.R
import com.example.sangallae.retrofit.models.NewProfile
import com.example.sangallae.retrofit.models.Profile
import com.example.sangallae.ui.MainActivity
import com.example.sangallae.ui.SplashActivity
import com.example.sangallae.utils.Constants
import com.example.sangallae.utils.RESPONSE_STATUS
import com.example.sangallae.utils.Usage
import com.google.firebase.auth.FirebaseAuth
import com.jeongdaeri.unsplash_app_tutorial.retrofit.RetrofitManager
import com.kakao.sdk.auth.TokenManager
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin

class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var pToolbar: androidx.appcompat.widget.Toolbar
    private var nick: String = ""
    private var hei: String = ""
    private var wei: String = ""
    private var pic: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        pToolbar = root.findViewById(R.id.profile_toolbar)
        (activity as AppCompatActivity).setSupportActionBar(pToolbar)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        profileViewModel.profileValue.observe(viewLifecycleOwner, Observer {
            view?.findViewById<TextView>(R.id.total_distance_view)?.text = it.total_distance
            view?.findViewById<TextView>(R.id.avg_distance_view)?.text = it.avg_distance
            view?.findViewById<TextView>(R.id.max_distance_view)?.text = it.max_distance
            view?.findViewById<TextView>(R.id.total_time_view)?.text = it.total_total_time
            view?.findViewById<TextView>(R.id.avg_time_view)?.text = it.avg_total_time
            view?.findViewById<TextView>(R.id.max_time_view)?.text = it.max_total_time
            view?.findViewById<TextView>(R.id.total_moving_time_view)?.text = it.total_moving_time
            view?.findViewById<TextView>(R.id.avg_moving_time_view)?.text = it.avg_moving_time
            view?.findViewById<TextView>(R.id.max_moving_time_view)?.text = it.max_moving_time
            view?.findViewById<TextView>(R.id.max_height_view)?.text = it.max_height
            view?.findViewById<TextView>(R.id.avg_height_view)?.text = it.avg_distance
            view?.findViewById<TextView>(R.id.max_speed_view)?.text = it.max_speed
            view?.findViewById<TextView>(R.id.avg_speed_view)?.text = it.avg_speed
            view?.findViewById<TextView>(R.id.max_pace_view)?.text = it.max_pace
            view?.findViewById<TextView>(R.id.avg_pace_view)?.text = it.avg_pace
            view?.findViewById<TextView>(R.id.total_uphill_view)?.text = it.total_total_uphill
            view?.findViewById<TextView>(R.id.max_uphill_view)?.text = it.max_total_uphill
            view?.findViewById<TextView>(R.id.avg_uphill_view)?.text = it.avg_total_uphill
            view?.findViewById<TextView>(R.id.total_downhill_view)?.text = it.total_total_downhill
            view?.findViewById<TextView>(R.id.max_downhill_view)?.text = it.max_total_downhill
            view?.findViewById<TextView>(R.id.avg_downhill_view)?.text = it.avg_total_downhill
            view?.findViewById<TextView>(R.id.total_calories_view)?.text = it.total_calories
            view?.findViewById<TextView>(R.id.avg_calories_view)?.text = it.avg_calories
            view?.findViewById<TextView>(R.id.nickname)?.text = it.nickname
            val heightWeight = it.user_height + "cm / " + it.user_weight + "kg"
            view?.findViewById<TextView>(R.id.height_weight)?.text = heightWeight

            if (it.picture != "no_image") {
                view?.findViewById<ImageView>(R.id.profile_image)?.let { it1 ->
                    Glide.with(this)
                        .load(it.picture)
                        .circleCrop()
                        .into(it1)
                }
            }
        })

        setHasOptionsMenu(true)


        // ????????? ??? ??? ?????? ?????? ??????
        profileLoadApiCall()

        // ????????? ?????? ??????
        val editBtn = root.findViewById<ImageButton>(R.id.edit_profile)
        editBtn.setOnClickListener {
            // Dialog ?????????
            val mDialogView = from(activity).inflate(R.layout.edit_profile_dialog, null)
            val mBuilder = activity?.let { it1 ->
                AlertDialog.Builder(it1)
                    .setView(mDialogView)
                //.setTitle("Login Form")
            }

            mDialogView.findViewById<EditText>(R.id.editNickname).setText(nick)
            mDialogView.findViewById<EditText>(R.id.editHeight).setText(hei)
            mDialogView.findViewById<EditText>(R.id.editWeight).setText(wei)

            if (pic != "no_image") {
                mDialogView.findViewById<ImageView>(R.id.popImageView)?.let {
                    Glide.with(this)
                        .load(pic)
                        .circleCrop()
                        .into(it)
                }
            }

            val mAlertDialog = mBuilder?.show()
            //?????? ??????
            val okButton = mDialogView.findViewById<Button>(R.id.successButton)
            okButton.setOnClickListener {
                // ????????? ????????? ?????? ????????????
                nick = mDialogView.findViewById<EditText>(R.id.editNickname).text.toString()
                hei = mDialogView.findViewById<EditText>(R.id.editHeight).text.toString()
                wei = mDialogView.findViewById<EditText>(R.id.editWeight).text.toString()


                // ????????? ???????????? ??????
                profileUpdateApiCall(nick, hei, wei)

                // ?????? ????????????
                view?.findViewById<TextView>(R.id.nickname)?.text =
                    nick + getString(R.string.profile_name)
                view?.findViewById<TextView>(R.id.height_weight)?.text = hei + "cm / " + wei + "kg"

                // ?????? ??????
                mAlertDialog?.dismiss()
            }
            //?????? ??????
            val cancelButton = mDialogView.findViewById<Button>(R.id.cancelBtn)
            cancelButton.setOnClickListener {
                mAlertDialog?.dismiss()
            }
        }

        val recordButton = root.findViewById<ImageButton>(R.id.my_record_btn)
        recordButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_myRecordFragment)
        }

        return root
    }

    private fun logout() {
        //Todo
        // ??????????????? ???????????? ????????????
        //?????????
        Log.d(Constants.TAG, TokenManager.instance.getToken().toString())

        UserApiClient.instance.logout { error ->
            if (error != null) {
                Log.e(Constants.TAG, "???????????? ??????. SDK?????? ?????? ?????????", error)
            } else {
                Log.i(Constants.TAG, "???????????? ??????. SDK?????? ?????? ?????????")
            }
        }
        if (OAuthLogin.getInstance() != null) { //?????????
            OAuthLogin.getInstance().logout(activity)
        }
        FirebaseAuth.getInstance().signOut() //??????

        Toast.makeText(this.context, R.string.logout, Toast.LENGTH_SHORT).show()
        val intent = Intent(activity, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.logout -> {
            logout()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun profileLoadApiCall() {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.profileLoad(completion = { status, profileItem ->
            when (status) {
                RESPONSE_STATUS.OKAY -> {
                    //Log.d(Constants.TAG, "PhotoCollectionActivity - searchPhotoApiCall() called ?????? ?????? / list.size : ${list?.size}")
                    if (profileItem != null) {
                        nick = profileItem.nickname
                        wei = profileItem.user_weight
                        hei = profileItem.user_height
                        pic = profileItem.picture

                        profileViewModel.setProfileValue(profileItem)
                    }
                }
                else -> {
                    Log.d(
                        Constants.TAG,
                        "ProfileFragment-OncreateView-profileLoadApiCall() profileItem: ${profileItem.toString()}"
                    )
                    Toast.makeText(this.context, "???????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    override fun onResume() {
        super.onResume()
        val activity = activity as MainActivity?
        activity?.hideUpButton()
    }
    private fun profileUpdateApiCall(newnick: String, newhei: String, newwei: String) {
        val retrofit = RetrofitManager(Usage.ACCESS)
        retrofit.profileUpdate(NewProfile(newnick, newhei, newwei), completion = { status ->
            when (status) {
                RESPONSE_STATUS.OKAY -> {
                    Log.d(Constants.TAG, "profileUpdateApiCall()")
                    Toast.makeText(this.context, "???????????? ???????????? ???????????????.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.d(
                        Constants.TAG,
                        "ProfileFragment-OncreateView-profileUpdateApiCall() Error"
                    )
                    Toast.makeText(this.context, "???????????? ??????????????? ??? ????????????.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}