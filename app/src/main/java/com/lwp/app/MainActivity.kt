package com.lwp.app

import android.app.Activity
import android.content.Context
import android.view.View
import com.lwp.lib.BaseActivity
import com.lwp.lib.BaseFragment
import com.lwp.lib.host.HostManager
import com.lwp.lib.mvp.view_model.BaseLiveDataViewModel
import kotlinx.coroutines.delay

open class MainActivity : BaseActivity() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        when (PackageManager.PERMISSION_GRANTED) {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) -> {
//            }
//            else -> {
//                ActivityCompat.requestPermissions(
//                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    0
//                )
//            }
//        }
//    }

    override fun getLayoutId(): Int = R.layout.activity_main
}

class MainModel {
    var live = "222222222"
}

class MainViewModel : BaseLiveDataViewModel<MainModel>() {
    fun haha(context: Context) {
        setState {
            live = "1111"
        }
        onUI {
            showLoading()
            onIo {
                delay(2000)
            }
            dismissLoading()
        }
    }

    override fun onStop() {
        setState {
            live = "333"
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn -> {
//                showLoading()
                showToast("11111111111")
//                onIO {
//                    HostManager.launch(
//                        HostManager.install("/sdcard/test.apk"),
//                        context() as Activity
//                    )
//                    onUi {
//                        dismissLoading()
//                    }
//                }
            }
        }
    }

    override fun reload() {
        hideError()
    }

    fun open(path: String) {
        if (context() is Activity)
            onIO {
                HostManager.launch(
                    HostManager.install(
                        "/sdcard/$path",
                    ),
                    context() as Activity
                )
            }
    }
}

class MainFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_main
    override fun create() {
        super.create()
    }
}
