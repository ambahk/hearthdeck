package hk.mba.games.hearthdeck.views;

import static hk.mba.games.tbone.elements.SemanticUi.*;
import hk.mba.games.hearthdeck.Client;
import hk.mba.games.hearthdeck.models.CardDb;
import hk.mba.games.hearthdeck.models.CardDto;
import hk.mba.games.hearthdeck.models.Deck;
import hk.mba.games.tbone.TBone;
import hk.mba.games.tbone.View;
import hk.mba.games.tbone.elements.UiCommon;
import hk.mba.games.tbone.elements.UiInput;
import hk.mba.games.tbone.elements.UiNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.java.html.js.JavaScriptBody;

import org.teavm.jso.JSObject;

public class DeckBuilder extends View {

	private final String playerClass;
	private final Deck deck;

	// display options
	private DeckCardGroupOption groupOption = DeckCardGroupOption.None;
	private String[] keywordFilter;
	private ClassFilter classFilter;
	private SortOption sortOption = SortOption.Cost;
	private final Set<Integer> costFilter = new TreeSet<>();
	private final Set<Integer> attackFilter = new TreeSet<>();
	private final Set<Integer> healthFilter = new TreeSet<>();

	public static enum DeckCardGroupOption {
		None, Type, GroupType, Group,
	}

	public static enum SortOption {
		Cost, Attack, Health
	}

	public static enum ClassFilter {
		Hero, Neutral, Both, Selected
	}

	// components
	private UiCommon swiperMenu;
	private UiCommon checkboxBar;
	private UiInput searchField;
	private UiCommon searchIcon;
	private UiSwiper cardSwiper;
	private UiCommon deckSummaryView;
	private UiInput deckNameField;
	private UiCommon deckCardListBody;

	private UiCommon fbSortLabel;

	// ====================================================================================================
	// Factory / constructors
	// ====================================================================================================

	private DeckBuilder(final String playerClass) {

		this.playerClass = playerClass;
		classFilter = ClassFilter.Hero;
		deck = new Deck(null, playerClass);
	}

	private DeckBuilder(final String playerClass, final String deckId) {

		this.playerClass = playerClass;
		classFilter = ClassFilter.Hero;
		deck = new Deck(deckId, playerClass);
	}

	public static DeckBuilder newDeck(final String playerClass) {
		return new DeckBuilder(playerClass);
	}

	public static DeckBuilder loadDeck(final String deckId) {
		final String[] split = TBone.split(deckId, ":");
		return new DeckBuilder(split[0], deckId);
	}

	// ====================================================================================================
	// View interface
	// ====================================================================================================

	@Override
	protected UiNode<?> onRender() {

		swiperMenu = menu("inverted").add(
				// buttons
				item("link active").text(playerClass)
						.data("filter", ClassFilter.Hero.name())
						.click(e -> setClassFilter(ClassFilter.Hero)),
				item("link").text("Neutral")
						.data("filter", ClassFilter.Neutral.name())
						.click(e -> setClassFilter(ClassFilter.Neutral)),
				item("link").text("Both").data("filter", ClassFilter.Both.name()).click(e -> setClassFilter(ClassFilter.Both)),
				item("link").text("Selected")
						.data("filter", ClassFilter.Selected.name())
						.click(e -> setClassFilter(ClassFilter.Selected)),
				// search input
				item("right").add(
						input("icon").add(
								searchField = textField("", x -> setKeywordFilter(x.value())).attr("placeholder", "Search..."),
								searchIcon = icon("search").click(e -> setKeywordFilter("")).style("cursor:pointer")
								)),
				// sorting menu
				item("right simple dropdown").add(
						fbSortLabel = span(sortOption.name()),
						icon("sort"),
						menu().add(
								item().text("By Cost").click(e -> sortBy(SortOption.Cost)),
								item().text("By Attack").click(e -> sortBy(SortOption.Attack)),
								item().text("By Health").click(e -> sortBy(SortOption.Health))
								)
						)
				);

		checkboxBar = menu("compact inverted").add(
				item().text("Cost"),
				addFilterLabel(costFilter, 0, "costFilter"),
				addFilterLabel(costFilter, 1, "costFilter"),
				addFilterLabel(costFilter, 2, "costFilter"),
				addFilterLabel(costFilter, 3, "costFilter"),
				addFilterLabel(costFilter, 4, "costFilter"),
				addFilterLabel(costFilter, 5, "costFilter"),
				addFilterLabel(costFilter, 6, "costFilter"),
				addFilterLabel(costFilter, 7, "costFilter"),
				addFilterLabel(costFilter, 8, "costFilter"),
				addFilterLabel(costFilter, 9, "costFilter"),

				item().text("Attack"),
				addFilterLabel(attackFilter, 0, "attackFilter"),
				addFilterLabel(attackFilter, 1, "attackFilter"),
				addFilterLabel(attackFilter, 2, "attackFilter"),
				addFilterLabel(attackFilter, 3, "attackFilter"),
				addFilterLabel(attackFilter, 4, "attackFilter"),
				addFilterLabel(attackFilter, 5, "attackFilter"),
				addFilterLabel(attackFilter, 6, "attackFilter"),
				addFilterLabel(attackFilter, 7, "attackFilter"),
				addFilterLabel(attackFilter, 8, "attackFilter"),
				addFilterLabel(attackFilter, 9, "attackFilter"),

				item().text("Health"),
				addFilterLabel(healthFilter, 1, "healthFilter"),
				addFilterLabel(healthFilter, 2, "healthFilter"),
				addFilterLabel(healthFilter, 3, "healthFilter"),
				addFilterLabel(healthFilter, 4, "healthFilter"),
				addFilterLabel(healthFilter, 5, "healthFilter"),
				addFilterLabel(healthFilter, 6, "healthFilter"),
				addFilterLabel(healthFilter, 7, "healthFilter"),
				addFilterLabel(healthFilter, 8, "healthFilter"),
				addFilterLabel(healthFilter, 9, "healthFilter")
				);

		cardSwiper = UiSwiper.create("allcards");

		deckSummaryView = segment();

		final UiCommon deckCardListView = table(16, "unstackable striped compact")
				.add(
						thead().add(tr().add(
								th(2).add(icon("sort", "link")).click(e -> rotateDeckCardGrouping()),
								th(2).text("Cost"),
								th(10).text("Name"),
								th(2).text("")
								)),
						deckCardListBody = tbody()
				);

		final UiCommon deckNameForm = form().add(
				fields().add(
						field(12, "fluid").add(
								deckNameField = textField(deck.name)
										.attr("placeholder", "Deck Name")
										.change(e -> deck.name = deckNameField.value())),
						field(4).add(button("fluid").text("Save")).click(e -> deck.save())
						)
				);

		return grid(16, "stackable page").add(
				column(11).add(grid(1).add(
						column().add(
								segment().add(swiperMenu, checkboxBar, cardSwiper),
								deckSummaryView
								)
						)),
				column(5).add(
						deckNameForm,
						deckCardListView
						)
				).ready(() -> execOnShow());
	}

	private void execOnShow() {

		cardSwiper.init();
		renderCardBrowser(cardSwiper);
		renderDeckCardList(false);
		renderDeckSummary();

		// loadDeck
		deck.load(() -> {
			deckNameField.value(deck.name);
			renderDeckCardList(true);
		});

	}

	// ====================================================================================================
	// Save / Update
	// ====================================================================================================

	// ====================================================================================================
	// Deck Summary
	// ====================================================================================================

	private void renderDeckSummary() {

		String curve = "";
		for (int i = 0; i < deck.manaCurve.length; i++) {
			if (i != 0) curve += "|";
			curve += deck.manaCurve[i];
		}

		deckSummaryView.clear().add(
				h3("Hero Class: " + playerClass),
				h5("Total/Varities: " + deck.total + "/" + deck.varity),
				h5("Mana Cost/Dust Cost: " + deck.manaCost + "/" + deck.dustCost),
				h5("Mana Curve: " + curve)
				);

		if (deck.minionTotal != 0) deckSummaryView.add(h5("Minions: " + deck.minionTotal + "/" + deck.minionVarity));
		if (deck.spellTotal != 0) deckSummaryView.add(h5("Spells: " + deck.spellTotal + "/" + deck.spellVarity));
		if (deck.weaponTotal != 0) deckSummaryView.add(h5("Weapons: " + deck.weaponTotal + "/" + deck.weaponVarity));

		deckSummaryView.add(
				h5("Card Sets"),
				span(TBone.join(deck.cardSets(), ", "))
				);
	}

	// ====================================================================================================
	// Deck Card List
	// ====================================================================================================

	private void rotateDeckCardGrouping() {
		// @formatter:off
		switch (groupOption) {
			case None: groupOption = DeckCardGroupOption.Group; break;
			case Group: groupOption = DeckCardGroupOption.GroupType ;break;
			case GroupType: groupOption = DeckCardGroupOption.Type; break;
			case Type: groupOption = DeckCardGroupOption.None; break;
		}
		// @formatter:on
		renderDeckCardList(true);
	}

	private void renderDeckCardList(final boolean changed) {

		if (changed == false) return;

		final List<DeckCardForDisplay> list = new ArrayList<>();
		deck.forEach(deckCard -> list.add(new DeckCardForDisplay(deckCard)));

		switch (groupOption) {
		case None:
			Collections.sort(list, (a, b) -> a.compareTo(b));
			break;
		case Type:
			Collections.sort(list, (a, b) -> {
				final int diffType = a.card.getType().compareTo(b.card.getType());
				if (diffType != 0) return diffType;
				return a.compareTo(b);
			});
			break;
		case GroupType:
			Collections.sort(list, (a, b) -> {
				final int diffGroup = a.group.compareTo(b.group);
				if (diffGroup != 0) return diffGroup;
				final int diffType = a.card.getType().compareTo(b.card.getType());
				if (diffType != 0) return diffType;
				return a.compareTo(b);
			});
			break;
		case Group:
			Collections.sort(list, (a, b) -> {
				final int diffGroup = a.group.compareTo(b.group);
				if (diffGroup != 0) return diffGroup;
				return a.compareTo(b);
			});
			break;
		}

		deckCardListBody.clear();
		for (final DeckCardForDisplay deckCardForDisplay : list) {
			deckCardListBody.add(tr().add(
					td().add(deckCardForDisplay.cardIcon())
							.style(deckCardForDisplay.groupStyle())
							.click(e -> renderDeckCardList(deck.rotateGroup(deckCardForDisplay.card))),
					td().text("" + deckCardForDisplay.card.getCost())
							.click(e -> renderDeckCardList(deck.remove(deckCardForDisplay.card))),
					td().text(deckCardForDisplay.card.getName())
							.click(e -> renderDeckCardList(deck.remove(deckCardForDisplay.card))),
					td().text("" + deckCardForDisplay.count)
							.click(e -> renderDeckCardList(deck.remove(deckCardForDisplay.card)))
					).style("cursor:pointer")
					);
		}

		renderDeckSummary();
	}

	private static class DeckCardForDisplay implements Comparable<DeckCardForDisplay> {

		public static enum Group {
			Core, Answer, Support, Alternative
		}

		public final CardDto card;
		public final int count;
		public final Group group;

		public DeckCardForDisplay(final Deck.DeckCard deckCard) {
			card = deckCard.getCardDto();
			count = deckCard.getCount();
			group = Group.valueOf(deckCard.getGroup());
		}

		@Override
		public int compareTo(final DeckCardForDisplay o) {
			final int diff = card.getCost() - o.card.getCost();
			if (diff != 0) return diff;
			return card.getName().compareTo(o.card.getName());
		}

		public String groupStyle() {
			// @formatter:off
			switch (group) {
				case Core : return "background-color:#0C0";
				case Answer : return "background-color:#8F8";
				case Support : return "background-color:#CFC";
				case Alternative : return "";
			}
			// @formatter:on
			return "";
		}

		public UiCommon cardIcon() {
			// @formatter:off
		String iconName = "";
		switch (card.getType()) {
			case "Weapon": iconName = "legal"; break;
			case "Minion": iconName = "user"; break;
			case "Spell": iconName = "wizard"; break;
		}
		String color = "black";
		switch (card.getRarity()) {
			case "Common": color = "black"; break;
			case "Rare": color = "blue"; break;
			case "Epic": color = "pink"; break;
			case "Legendary": color = "yellow"; break;
		}
		// @formatter:on
			return icon(iconName, "tiny " + color);
		}
	}

	// ====================================================================================================
	// Filters and CardBrowser
	// ====================================================================================================

	private void setKeywordFilter(final String keywords) {
		final String k = keywords.trim().toLowerCase();

		if ("".equals(k)) searchIcon.jquery().removeClass("remove").addClass("search");
		else searchIcon.jquery().removeClass("search").addClass("remove");

		searchField.value(k);
		keywordFilter = TBone.split(k, " ");
		renderCardBrowser(cardSwiper);
	}

	private void setClassFilter(final ClassFilter newFilter) {

		// changed?
		if (newFilter.equals(classFilter)) return;

		// update menu
		classFilter = newFilter;
		highlightFilterButton();
		renderCardBrowser(cardSwiper);
	}

	private void highlightFilterButton() {

		swiperMenu.jquery()
				.children()
				.removeClass("active")
				.filter("[data-filter='" + classFilter + "']")
				.addClass("active");
	}

	private void sortBy(final SortOption option) {
		sortOption = option;
		fbSortLabel.jquery().text(sortOption.name());
		renderCardBrowser(cardSwiper);
	}

	private void renderCardBrowser(final UiSwiper swiper) {

		swiper.clear();

		Client.cardDb.listen(db -> {

			final List<UiCommon> slides = new ArrayList<>();

			// @formatter:off
			switch (sortOption) {
				case Cost: 	db.forEach        (card -> insertCard(slides, card)); break;
				case Attack:db.forEachByAttack(card -> insertCard(slides, card)); break;
				case Health:db.forEachByHealth(card -> insertCard(slides, card)); break;
			}
			// @formatter:on

			swiper.add(slides.toArray(new UiCommon[0]));
		});
	}

	private void insertCard(final List<UiCommon> slides, final CardDto card) {
		if (!card.isCollectible()) return;

		// filter by player class
		final String cardClass = card.getPlayerClass();
		if (!cardClass.equals(playerClass) && !cardClass.equals("Neutral")) return;

		// @formatter:off
		switch (classFilter) {
			case Both: 		break;
			case Hero: 		if (cardClass.equals("Neutral")) return; break;
			case Neutral: 	if (!cardClass.equals("Neutral")) return; break;
			case Selected: 	if (!deck.contains(card.getId())) return; break;
		}
		// @formatter:on

		// filter by keywords
		if (keywordFilter != null) {
			for (final String f : keywordFilter) {
				if (!TBone.contains(card.getFullText(), f)) return;
			}
		}

		// filter by attack/health
		if (costFilter.size() != 0) {
			if (card.getCost() > 9) {
				if (!costFilter.contains(9)) return;
			} else {
				if (!costFilter.contains(card.getCost())) return;
			}
		}
		if (attackFilter.size() != 0) {
			if (card.getAttack() > 9) {
				if (!attackFilter.contains(9)) return;
			} else {
				if (!attackFilter.contains(card.getAttack())) return;
			}
			if (!card.getType().equals("Minion")) return;
		}
		if (healthFilter.size() != 0) {
			if (card.getHealth() > 9) {
				if (!healthFilter.contains(9)) return;
			} else {
				if (!healthFilter.contains(card.getHealth())) return;
			}
			if (!card.getType().equals("Minion")) return;
		}

		// add image to swiper
		slides.add(div("swiper-slide").add(
				image(CardDb.imageUrl("enus", card.getId())).click(e -> renderDeckCardList(deck.add(card)))
				));
	}

	// ====================================================================================================
	// Swiper
	// ====================================================================================================

	public static final class UiSwiper extends UiNode<UiSwiper> {

		private final String prefix;
		private JSObject jsVar;

		public UiSwiper(final String prefix, final UiNode<?> domNode) {
			super(domNode);
			this.prefix = prefix;
		}

		@JavaScriptBody(args = { "swiper", "slides" }, body = "swiper.appendSlide(slides)")
		private static native void appendSlide(JSObject var, JSObject... slides);

		@Override
		public UiSwiper add(final UiNode<?>... slides) {
			if (slides.length != 0) {
				final JSObject[] nodes = new JSObject[slides.length];
				for (int i = 0; i < slides.length; i++) {
					nodes[i] = slides[i].node();
				}
				appendSlide(jsVar, nodes);
			}
			return this;
		}

		@JavaScriptBody(args = { "swiper" }, body = "swiper.removeAllSlides()")
		private static native void removeAllSlides(JSObject var);

		@Override
		public UiSwiper clear() {
			removeAllSlides(jsVar);
			return this;
		}

		@JavaScriptBody(args = { "cId", "pId", "sId" }, body = "return new Swiper (cId, { "
				// + "pagination:pId,"
				+ "grabCursor:true,"
				+ "paginationClickable:true,"
				+ "slidesPerView:4,"
				+ "scrollbar:sId,"
				+ "scrollbarHide:true,"
				+ "freeMode:true,"
				+ "mousewheelControl:true,"
				+ "mousewheelForceToAxis:true,"
				+ "})")
		private static native JSObject initSwiper(String container, String pagination, String scrollbar);

		public UiSwiper init() {
			jsVar = initSwiper("#" + prefix + "-container", "#" + prefix + "-pagination", '#' + prefix + "-scrollbar");
			return this;
		}

		public static UiSwiper create(final String prefix) {

			final UiCommon container = div("swiper-container").id(prefix + "-container").add(
					div("swiper-wrapper"),
					div("swiper-pagination").id(prefix + "-pagination"),
					div("swiper-scrollbar").id(prefix + "-scrollbar"));

			return new UiSwiper(prefix, container);
		}
	}

	// ====================================================================================================
	// Attack/Health Filter
	// ====================================================================================================

	private UiCommon addFilterLabel(final Set<Integer> filter, final int value, final String filterClass) {

		final String label = (value == 9) ? 9 + "+" : value + "";
		final UiCommon ele = item(filterClass + " link active").text(label);

		ele.click(e -> {
			if (filter.size() == 0 || !filter.contains(value)) {
				filter.clear();
				TBone.jquery("." + filterClass).removeClass("active");
				filter.add(value);
				TBone.jquery(ele).addClass("active");
			} else {
				filter.clear();
				TBone.jquery("." + filterClass).addClass("active");
			}
			renderCardBrowser(cardSwiper);
		});
		return ele;
	}
}
