//package com.mio.base;
//
//import android.util.Log;
//
//import com.yanzhenjie.andserver.annotation.GetMapping;
//import com.yanzhenjie.andserver.annotation.PathVariable;
//import com.yanzhenjie.andserver.annotation.PostMapping;
//import com.yanzhenjie.andserver.annotation.PutMapping;
//import com.yanzhenjie.andserver.annotation.QueryParam;
//import com.yanzhenjie.andserver.annotation.RequestMapping;
//import com.yanzhenjie.andserver.annotation.RequestParam;
//import com.yanzhenjie.andserver.annotation.RestController;
//
//@RestController
//@RequestMapping(path = "/user")
//public class UserController {
//    private static final String TAG = "UserController";
//
//    @GetMapping("/login")
//    public String login(@RequestParam("account") String account,
//                        @RequestParam("password") String password) {
//
//        return "Successful.";
//    }
//
////    @GetMapping(path = "/{userId}")
////    public User info(@PathVariable("userId") String userId,
////                     @QueryParam("fields") String fields) {
////        return user;
////    }
//
////    @PutMapping(path = "/{userId}")
////    public void modify(@PathVariable("userId") String userId,
////                       @RequestParam("age") int age) {
////        Log.d(TAG, "modify: ");
////    }
//}