package com.falcon.turingx.widget.listener

/**
 *  O PaginationScrollListener é a classe responsável pelo callback de scroll de uma recycler view.
 *
 *  @param onScroll é chamado sempre que ocorre um evento de scroll dentro da recycler view, onScroll
 *  também pode ser ignorado caso não exista necessidade de escutar os eventos.
 *  @param onLoadMore é chamado sempre que o penultimo item da lista da recycler view for mostrado
 *  na tela.
 *
 * */
class TXPaginationScrollListener(
    onScroll: () -> Unit = {},
    onLoadMore: (totalItemCount: Int) -> Unit = {}
) : IPaginationScrollListener(onScroll, onLoadMore) {

    // Intervalo de tempo de chamada do onLoadMore
    private var lockTime = 500L

    // Instante de tempo da ultima chamada do onLoadMore.
    private var lastScrolledTime = 0L

    /**
     * Sempre que ocorrer um evento de scroll dentro da recycler view essa função será chamada.
     *
     * Será feito uma verificação de que o item que esta sendo exibido na tela do dispositivo é o
     * penultimo da lista, e também será verificado se dy é maior que zero, ou seja, verifica se o
     * scroll dentro da recycler view é de cima para baixo. Se esses requisitos forem atendidos será
     * verifacado se teve algum evento de onLoadMore no intervalo de tempo de lockTime, se não tiver
     * ocorrido atualiza o instante de tempo da ultima chamada do loadMore e chame ele.
     *
     * */
    override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
        onScroll()
        val layoutManager = (recyclerView.layoutManager as? androidx.recyclerview.widget.LinearLayoutManager)
        layoutManager?.let {
            if (layoutManager.itemCount.minus(1) == layoutManager.findLastVisibleItemPosition() && dy > 0 && !isLocked()) {
                updateTime()
                onLoadMore(layoutManager.itemCount)
            }
        }
    }

    /**
     * A função isLocked retorna se o onLoadMore está habilitado ou não.
     *
     * */
    private fun isLocked(): Boolean {
        return System.currentTimeMillis() - lastScrolledTime < lockTime
    }

    /**
     * A função updateTime atualiza o tempo da ultima chamada do onLoadMore.
     *
     * */
    private fun updateTime() {
        lastScrolledTime = System.currentTimeMillis()
    }

}