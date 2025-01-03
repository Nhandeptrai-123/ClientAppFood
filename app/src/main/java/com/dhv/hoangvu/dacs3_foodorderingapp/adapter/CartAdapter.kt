package com.dhv.hoangvu.dacs3_foodorderingapp.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.nfc.Tag
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dhv.hoangvu.dacs3_foodorderingapp.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.coroutineContext

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<String>,
    private val cartItemPrices: MutableList<String>,
    private val cartDescription:MutableList<String>,
    private val cartImages: MutableList<String>,
    private val cartQuantity: MutableList<Int>,
//    private val cartIngredient: MutableList<String>,
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    //khởi tạo firebase
    private val auth = FirebaseAuth.getInstance()

    init {
        val database = FirebaseDatabase.getInstance()
        val useId = auth.currentUser?.uid?:""
        val cartItemNumber = cartItems.size

        itemQuantities = IntArray(cartItemNumber){1}
        cartItemReference = database.reference.child("ClientUser").child(useId).child("GioHang")


    }
    companion object{
        private var  itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemReference: DatabaseReference
    }

//    private val itemQuantities = IntArray(cartItems.size) { 1 } --> DElete

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {

        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                cartFoodName.text = cartItems[position]
                cartItemPrice.text = cartItemPrices[position]

                //   sữ dụng Glide để load ảnh
                val uriString = cartImages[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(cartImage)



                cartItemQuantity.text = quantity.toString()
                minusButton.setOnClickListener {
                    decreaseQuantity(position)
                }
                plusButton.setOnClickListener {
                    increaseQuantity(position)
                }

                deleteButton.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }
                }

            }
        }

        private fun decreaseQuantity(position: Int) {
            if (itemQuantities[position] > 1) {
                itemQuantities[position]--
                cartQuantity[position] = itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun increaseQuantity(position: Int) {
            if (itemQuantities[position] < 10) {
                itemQuantities[position]++
                cartQuantity[position] = itemQuantities[position]
                binding.cartItemQuantity.text = itemQuantities[position].toString()
            }
        }

        private fun deleteItem(position: Int) {
           val positionRetrieve = position
            getUniqueKeyAtPosition(positionRetrieve){uniquekey ->
                if (uniquekey != null){
                    removeItem(position,uniquekey)
                }
            }
        }
    }

    private fun removeItem(position: Int, uniquekey: String) {
        if(uniquekey != null){
            cartItemReference.child(uniquekey).removeValue().addOnCompleteListener{
                cartItems.removeAt(position)
                cartImages.removeAt(position)
                cartDescription.removeAt(position)
                cartQuantity.removeAt(position)
                cartItemPrices.removeAt(position)
//                cartIngredient.removeAt(position)

                Toast.makeText(context, "Xoá thành công", Toast.LENGTH_SHORT).show()
                //uppdate lại số lượng
                itemQuantities = itemQuantities.filterIndexed { index, i -> index != position }.toIntArray()
                notifyItemRemoved(position)
                notifyItemRangeChanged(position,cartItems.size)
            }.addOnFailureListener {
                Toast.makeText(context, "Xoá thất bại", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String) -> Unit) {
        cartItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniquekey: String? = null
                // vòng lặp qua các nhánh con snapshot
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if (index == positionRetrieve) {
                        uniquekey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                val key = uniquekey ?: "" // Nếu uniquekey là null, sử dụng chuỗi trống
                // Gọi onComplete ở đây sau khi uniquekey đã được xác định
                onComplete(key)
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi
            }
        })
    }

    // Hàm cập nhật dữ liêu Quantity
    fun getUpdateItemsQuamtities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }

}