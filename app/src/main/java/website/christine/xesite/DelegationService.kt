package website.christine.xesite

import com.google.androidbrowserhelper.locationdelegation.LocationDelegationExtraCommandHandler
import com.google.androidbrowserhelper.trusted.DelegationService

class DelegationService : DelegationService() {
    override fun onCreate() {
        super.onCreate()
        registerExtraCommandHandler(LocationDelegationExtraCommandHandler())
    }
}