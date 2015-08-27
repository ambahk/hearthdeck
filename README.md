# Summary

A web applications that lets Hearthstone players build and store decks.  The web app is server-less.  The decks are stored in the local storage of the browser.

# Installation

Make sure that you are using Java 8.

```
mvn package
```

# Usage

Start the HTTP Server

```
cd deck-builder/public
python -m SimpleHTTPServer
```

Then optn the following URL in a browser

http://localhost:8000/

# Things to try

Create a new deck.
Add some cards to the deck.
Save the deck.
Go back to the *Decks* page to see the deck that was created.
Go to *Cards* page to browse the card list.

# Database

The Hearthstone card database is downloadable from http://hearthstonejson.com

# Card Images

Card images are retrieved from http://wow.zamimg.com/images/hearthstone/cards/

# TODO

* retrieve latest card database directly from hearthstonejson.com
* store the entire card database in localStorage as well, and add a refresh button or use the HEAD method to check for any changes to the card database
* import / export functions
* save to parse.com with facebook oauth authentication
