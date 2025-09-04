package com.example.mtt_rental.repo

object UserRepo {
    var idUser: String = ""
    var profileName: String = ""
    var email: String = ""
    var phoneNumber: String = ""
    var userType: String = ""
    var address: String? = null
    fun updateUser(idUser:String, profileName:String, email:String, phoneNumber:String, userType:String, address:String?){
        this.idUser = idUser
        this.profileName = profileName
        this.email = email
        this.phoneNumber = phoneNumber
        this.userType = userType
        this.address = address
    }
}
