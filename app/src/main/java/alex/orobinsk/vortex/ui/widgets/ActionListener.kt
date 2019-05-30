package alex.orobinsk.vortex.ui.widgets

interface ActionListener<T>: ItemsProvider {
    fun onClick(data: T) {}
}
interface ItemsProvider {
/*
    abstract fun<T> getItems(): MutableLiveData<T>
*/
}