package com.rpn.adminmosque.api

import com.rpn.adminmosque.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {



    @GET("calendar")
    suspend fun getPrayerTime(@Query("latitude") latitude: String? = "22.352795",
                                    @Query("longitude") longitude: String? = "91.783039",
                                    @Query("method") method: String? = "2",
                                    @Query("month") month: String? = "7",
                                    @Query("year") year: String? = "2022") : Response<PrayerTime>
/*

    @GET("new-order")
    suspend fun getMyNewOrders() : Response<MyNewOrders>

    @GET("reservation-list")
    suspend fun getMyNewReservation(@Query("status") status: String? = "0") : Response<MyReservations>

    @GET("order-list")
    suspend fun getOrders() : Response<MyNewOrders>

    @GET("accept-order-list")
    suspend fun getAcceptOrders() : Response<MyNewOrders>

    @GET("reservation-list")
    suspend fun getReservations() : Response<MyReservations>



    @GET("/new-orders")
    suspend fun getNewOrders() : Response<NewOrders>


    @GET("/customers-{customerId}")
    suspend fun getCustomer(@Path("customerId") customerId: String?) : Response<CustomerInfo>


    @POST("update-{orderId}-{orderResponse}")
    suspend fun orderResponse(@Path("orderId") orderId: String?,@Path("orderResponse") orderResponse: String?): Response<Any?>?

//    @POST("accept-order?id={orderId}")
//    suspend fun acceptOrderResponse(@Path("orderId") orderId: String?): Response<Any?>?


    @POST("accept-order")
    suspend fun acceptOrderResponse(@Query("id") id: Int?): Response<MyNewOrders>

    @POST("order-delete")
    suspend fun rejectOrderResponse(@Query("id") id: Int?): Response<Any?>?

    @POST("complete-order")
    suspend fun completeOrderResponse(@Query("id") id: Int?): Response<MyNewOrders>

    @POST("reservation-status")
    suspend fun reservationResponse(@Query("reservation_id") reservation_id: Int?,@Query("status") status: Int?): Response<Any?>?

*/
}