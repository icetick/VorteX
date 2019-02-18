package alex.orobinsk.vortex.ui.widgets

import androidx.lifecycle.MutableLiveData

interface ActionListener<T>: ItemsProvider {
    open fun onClick(data: T) {}

}
interface ItemsProvider {
/*
    abstract fun<T> getItems(): MutableLiveData<T>
*/
}