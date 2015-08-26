package hk.mba.games.hearthdeck;

import static hk.mba.games.tbone.elements.UiNode.*;
import hk.mba.games.hearthdeck.models.CardDb;
import hk.mba.games.hearthdeck.views.CardBrowser;
import hk.mba.games.hearthdeck.views.DeckBrowser;
import hk.mba.games.hearthdeck.views.DeckBuilder;
import hk.mba.games.tbone.TBone;
import hk.mba.games.tbone.wrappers.PageCallbackWithId;

public class Client {

	private static final ContentGrid content = new ContentGrid();
	public static final CardDb cardDb = new CardDb();

	public static void main(final String... args) {

		TBone.startObserver();
		body().add(
				new HeaderNav(),
				content,
				new FooterGrid());

		setupRouter();
		cardDb.init();
	}

	// ====================================================================================================
	// Router
	// ====================================================================================================

	private static void setupRouter() {

		// @formatter:off
		TBone.page("/cards",					(Runnable) () -> content.show(new CardBrowser()));
		TBone.page("/decks",					(Runnable) () -> content.show(new DeckBrowser()));
		TBone.page("/builder/:id",				(PageCallbackWithId) id -> content.show(DeckBuilder.newDeck(id))); // PlayerClass
		TBone.page("/deck/:id",					(PageCallbackWithId) id -> content.show(DeckBuilder.loadDeck(id))); // Deck ID

		TBone.page().redirect("*", "/decks");
		// @formatter:on

		TBone.pageStart();
	}

	// ====================================================================================================
	// convenient methods for calling routes
	// alternatively, we can call TBone.show() to save some boilerplate code, but then the routes will be scattered all
	// around the code base.
	// ====================================================================================================

	public static void gotoCards() {
		TBone.show("/cards");
	}

	public static void gotoDecks() {
		TBone.show("/decks");
	}

	public static void gotoDeckBuilder(final String playerClass) {
		TBone.show("/builder/" + playerClass);
	}

	public static void gotoDeck(final String deckId) {
		TBone.show("/deck/" + deckId);
	}

	public static void gotoTeams() {
		gotoDecks();
	}

}
