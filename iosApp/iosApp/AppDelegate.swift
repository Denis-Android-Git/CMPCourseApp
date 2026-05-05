//
//  AppDelegate.swift
//  iosApp
//
//  Created by mac on 23.04.2026.
//

import Foundation
import ComposeApp
import UIKit
import UserNotifications
import FirebaseCore
import FirebaseMessaging

class AppDelegate : NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate, MessagingDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
            FirebaseApp.configure()
            
            UNUserNotificationCenter.current().delegate = self
            Messaging.messaging().delegate = self
            print("check_ios_token: didFinishLaunchingWithOptions")

            return true
        }
        
        func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
            Messaging.messaging().apnsToken = deviceToken
            print("check_ios_token: didRegisterForRemoteNotificationsWithDeviceToken")
            refreshToken()
        }
        
        func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
            print("check_ios_token: Failed to register for push notifications: \(error)")
        }
        
        func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
            guard let token = fcmToken, !token.isEmpty else {
                refreshToken()
                return
            }
            
            UserDefaults.standard.set(token, forKey: "FCM_TOKEN")
            IosDeviceTokenHolderBridge.shared.updateToken(token: token)
            print("check_ios_token: didReceiveRegistrationToken")

        }
        
        func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable : Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
            Messaging.messaging().appDidReceiveMessage(userInfo)
            completionHandler(.newData)
            print("check_ios_token: didReceiveRemoteNotification")
        }
        
        func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
            completionHandler([.banner])
            print("check_ios_token: userNotificationCenter willPresent")

        }
        
        func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
            let userInfo = response.notification.request.content.userInfo
            
            if let chatId = userInfo["chatId"] as? String {
                let deepLinkUrl = "chirp://chat_detail/\(chatId)"
                ExternalUriHandler.shared.onNewUri(uri: deepLinkUrl)
            }
            print("check_ios_token: userNotificationCenter didReceive")
            completionHandler()
        }
        
        func refreshToken() {
            Task {
                do {
                    let fcmToken = try await Messaging.messaging().token()
                    
                    UserDefaults.standard.set(fcmToken, forKey: "FCM_TOKEN")
                    IosDeviceTokenHolderBridge.shared.updateToken(token: fcmToken)
                    print("check_ios_token: refreshToken()")

                } catch {
                    print("check_ios_token: refreshToken() catch: \(error.localizedDescription)")
                }
            }
        }
}
