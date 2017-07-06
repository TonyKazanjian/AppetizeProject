# My project's README
This project takes an image from the user's photo gallery and posts it to Dropbox or Box cloud storage.

I wanted to give users a choice for which service to use, and it looked like CloudRail was a good solution that integrated both APIs into one. Documentation can be found at https://cloudrail.com/integrations/interfaces/CloudStorage.

# Structure
I used an MVP architecture for this app just to show how I would set up a project in order to be unit tested. The package structure of Homescreen, Network, and Utils divides classes by category or screen.

# Limitations
Using the free version of the CloudRail API forces users to click through an authentication in an embedded browser. Also, when using Google APIs via authenticating Dropbox with a Google account, our first call results in an "Unrecognized Content-Security-Policy directive 'worker-src'" error.
 Subsequent calls work once the user has logged in, however. If I had more time, I would do research on how to circumvent this.