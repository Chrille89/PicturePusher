// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
var notifyApp = angular.module('notifyApp', ['ionic','ngCordova']);

notifyApp.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {

    if(window.cordova && window.cordova.plugins.Keyboard) {
      // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
      // for form inputs)
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);

      // Don't remove this line unless you know what you are doing. It stops the viewport
      // from snapping when text inputs are focused. Ionic handles this internally for
      // a much nicer keyboard experience.
      cordova.plugins.Keyboard.disableScroll(true);
    }
    if(window.StatusBar) {
      StatusBar.styleDefault();
    }
});
});

notifyApp.controller('notifyCtrl',['$scope','$cordovaVibration','$cordovaLocalNotification',function($scope,$cordovaVibration,$cordovaLocalNotification){

     console.log("Lade WebCam-Datenstrom...");

      // the last received msg
      $scope.warning = {};
      
      var chatEvents = new EventSource("http://IP:8080/PicturePusher/api/events/stream");
      
       // When data is received
      chatEvents.onmessage = function (event) {
              
                var url = "http://IP:8080/PicturePusher/api/events/get/"+event.data;
                 var json= { 
                      "data": event.data,
                      "url": url,
                      "title": "Zu Hause hat sich etwas bewegt",
                      "message": "Für Details klicken", 
                    };
                     $scope.warning[event.data] = json ;
                     $scope.$apply();
                         
                     $cordovaVibration.vibrate(100);

                var alarmTime = new Date();
                  $cordovaLocalNotification.schedule({
                    id: "1",
                    at: alarmTime,
                    text: "Zu Hause hat sich etwas bewegt!",
                    title: "Einbrecher?",
                    badge:      1,
                    autoCancel: true,
                    ongoing:    true, 
                    sound: "file://demonstrative.mp3"
                    }).then(function () {
                      console.log("The notification has been set");
                    });      
          };

      $scope.clearList = function(){
          console.log("Lösche Liste...");
          $scope.warning = {};
      }

      $scope.clickOnWarning = function(json){
          console.log("Remove entry and open URL: "+json.url+"...");
          delete $scope.warning[json.data];
          window.open(json.url,'_blank'); 
      };
  }]);
