import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shoestoreapp.R
import com.example.shoestoreapp.adapter.OrderProductsAdapter
import com.example.shoestoreapp.data.model.Order
import com.example.shoestoreapp.data.model.ProductDetailItem
import java.text.NumberFormat
import java.util.Locale

class OrdersAdapter(
    private val onOrderClick: (Pair<Order, List<ProductDetailItem>>) -> Unit
) : ListAdapter<Pair<Order, List<ProductDetailItem>>, OrdersAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_admin, parent, false)
        return OrderViewHolder(view, onOrderClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(
        itemView: View,
        private val onOrderClick: (Pair<Order, List<ProductDetailItem>>) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        private val tvUserId: TextView = itemView.findViewById(R.id.tvUserId)
        private val rvOrderProducts: RecyclerView = itemView.findViewById(R.id.rvOrderProducts)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvTotalPayment: TextView = itemView.findViewById(R.id.tvTotalPayment)
        private val tvPaymentMethod: TextView = itemView.findViewById(R.id.tvPaymentMethod)
        private val btnViewDetails: Button = itemView.findViewById(R.id.btnViewDetails)
        private val productsAdapter = OrderProductsAdapter()

        init {
            rvOrderProducts.apply {
                layoutManager = LinearLayoutManager(itemView.context)
                adapter = productsAdapter
            }
        }

        fun bind(orderWithProducts: Pair<Order, List<ProductDetailItem>>) {
            val (order, products) = orderWithProducts

            tvOrderId.text = "Mã đơn hàng: ${order.id}"
            tvUserId.text = "Mã khách hàng: ${order.userId}"
            tvStatus.text = "${order.status}"
            tvTotalPayment.text = "Tổng tiền: ${formatCurrency(order.totalPayment)}"
            tvPaymentMethod.text = "Thanh toán: ${order.paymentMethod}"

            productsAdapter.submitList(products)

            btnViewDetails.setOnClickListener {
                onOrderClick(orderWithProducts)
            }


        }

        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            return format.format(amount)
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Pair<Order, List<ProductDetailItem>>>() {
        override fun areItemsTheSame(
            oldItem: Pair<Order, List<ProductDetailItem>>,
            newItem: Pair<Order, List<ProductDetailItem>>
        ): Boolean {
            return oldItem.first.id == newItem.first.id
        }

        override fun areContentsTheSame(
            oldItem: Pair<Order, List<ProductDetailItem>>,
            newItem: Pair<Order, List<ProductDetailItem>>
        ): Boolean {
            return oldItem == newItem
        }
    }
}