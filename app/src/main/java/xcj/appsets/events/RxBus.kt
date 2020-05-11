package xcj.appsets.events

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay

class RxBus {
    private var bus: Relay<Event?> = PublishRelay.create()

    fun getBus(): Relay<Event?>? = bus
}