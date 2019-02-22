

'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((change, context) => {

  const user_id = context.params.user_id;
  const notification_id = context.params.notification_id;


 //Commentary on firebase
  console.log('We have a notification to send to : ', context.params.user_id);

  if(!change.after.val()) {
      return console.log('A Notification has been deleted from the database : ' + context.params.notification_id);
  }

  const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');

  return fromUser.then(fromUserResult => {

   const from_user_id = fromUserResult.val().from;

   console.log('You have new notification from : ', from_user_id);

   const userQuery = admin.database().ref(`Users/${from_user_id}/name`).once('value');

   return userQuery.then(userResult => {

     const userName = userResult.val();

     const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');

     return deviceToken.then(result =>  {

       const token_id = result.val();

       // send notification to user
       const payload = {
           notification: {
               title : "Follower Request",
               body: `${userName} sent you a friend request`,
               icon: "default",
              click_action: "ProfileActivity"
           },
          data : {

            from_user_id : from_user_id

          }
       };

       return admin.messaging().sendToDevice(token_id, payload).then(response => {
          console.log('This was the notification Feature');
       });

     });

   });

  });

  /*const deviceToken = admin.database().ref(`/Users/${user_id}/device_token`).once('value');

  return deviceToken.then(result =>  {

    const token_id = result.val();

    // send notification to user
    const payload = {
        notification: {
            title : "Follower Request",
            body: "You've received a new friend request",
            icon: "default"
        }
    };

    return admin.messaging().sendToDevice(token_id, payload).then(response => {
       console.log('This was the notification Feature');
    });

  });*/

});



//const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
