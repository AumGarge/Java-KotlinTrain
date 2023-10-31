import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

data class FoodItem(val name: String, val price: Double)

open class Customer(val name: String, val phoneNumber: String, val address: String, val customerId: String) {
    open fun getDiscount(): Double {
        return 0.0
    }
}

class RegularCustomer(name: String, phoneNumber: String, address: String, customerId: String) : Customer(name, phoneNumber, address, customerId) {
    override fun getDiscount(): Double {
        return 0.15
    }
}

class PremiumCustomer(name: String, phoneNumber: String, address: String, customerId: String) : Customer(name, phoneNumber, address, customerId) {
    override fun getDiscount(): Double {
        return 0.25
    }
}

class GuestCustomer(name: String, phoneNumber: String, address: String, customerId: String) : Customer(name, phoneNumber, address, customerId)

data class Order(val foodItems: List<FoodItem>) {
    fun getTotalPrice(): Double {
        return foodItems.sumByDouble { it.price }
    }
}

object Menu {
    val foodItems = listOf(
        FoodItem("Pizza", 100.0),
        FoodItem("Burger", 80.0),
        FoodItem("Pasta", 90.0),
        FoodItem("Salad", 50.0),
        FoodItem("Thali", 120.0),
        // Add more items as needed
    )

    fun displayMenu() {
        println("Menu:")
        foodItems.forEachIndexed { index, foodItem ->
            println("${index + 1}. ${foodItem.name} - Rs${foodItem.price}")
        }
    }

    fun getFoodItem(index: Int): FoodItem? {
        return if (index >= 1 && index <= foodItems.size) {
            foodItems[index - 1]
        } else {
            null
        }
    }
}

fun main() {
    println("Enter your name:")
    val name = readLine().toString()

    println("Enter your phone number:")
    val phoneNumber = readLine().toString()

    println("Enter your address:")
    val address = readLine().toString()

    println("Enter your customer ID:")
    val customerId = readLine().toString()

    val customerType = when {
        customerId.startsWith("R") -> 1
        customerId.startsWith("P") -> 2
        customerId.startsWith("G") -> 3
        else -> throw IllegalArgumentException("Invalid customer ID.")
    }

    val customer: Customer = when (customerType) {
        1 -> RegularCustomer(name, phoneNumber, address, customerId)
        2 -> PremiumCustomer(name, phoneNumber, address, customerId)
        3 -> GuestCustomer(name, phoneNumber, address, customerId)
        else -> throw IllegalArgumentException("Invalid customer type.")
    }

    val order = mutableListOf<FoodItem>()

    Menu.displayMenu()

    while (true) {
        println("Enter the item number you want to order (0 to finish): ")
        val userInput = readLine()?.toIntOrNull()

        if (userInput == 0) {
            break
        }

        val foodItem = Menu.getFoodItem(userInput ?: 0)
        if (foodItem != null) {
            order.add(foodItem)
        } else {
            println("Invalid input. Please try again.")
        }
    }

    val finalOrder = Order(order)
    println("\nCustomer Information:")
    println("Name: ${customer.name}")
    println("Phone Number: ${customer.phoneNumber}")
    println("Address: ${customer.address}")

    println("\nYour order:")
    finalOrder.foodItems.forEachIndexed { index, foodItem ->
        println("${index + 1}. ${foodItem.name} - Rs${foodItem.price}")
    }

    val discount = customer.getDiscount()
    val totalDiscountedPrice = finalOrder.getTotalPrice() * (1 - discount)
    println("Total (with ${discount * 100}% discount): Rs$totalDiscountedPrice")

    // Save details to a file
    val fileName = when (customer) {
        is RegularCustomer -> "regular.txt"
        is PremiumCustomer -> "premium.txt"
        is GuestCustomer -> "guest.txt"
        else -> throw IllegalArgumentException("Invalid customer type.")
    }

    val file = File(fileName)

    BufferedWriter(FileWriter(file, true)).use { writer -> // append mode
        writer.write("Customer Information:\n")
        writer.write("Name: ${customer.name}\n")
        writer.write("Phone Number: ${customer.phoneNumber}\n")
        writer.write("Address: ${customer.address}\n\n")

        writer.write("Order Details:\n")
        finalOrder.foodItems.forEachIndexed { index, foodItem ->
            writer.write("${index + 1}. ${foodItem.name} - Rs${foodItem.price}\n")
        }

        writer.write("\nTotal (with ${discount * 100}% discount): Rs$totalDiscountedPrice\n\n")
    }

    println("Order details appended to $fileName")
}
