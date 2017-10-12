package com.sotrh.geocodeapp

import android.support.v7.widget.SearchView
import io.reactivex.Observable

/**
 * Created by benjamin on 10/12/17
 */

fun SearchView.observeOnQueryTextSubmit(): Observable<String> = Observable.create<String> { emitter ->
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            if (query == null) emitter.onNext("")
            else emitter.onNext(query)
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean = false
    })
}

fun Double.toRadians() = Math.toRadians(this)
fun Double.km() = this / 1000