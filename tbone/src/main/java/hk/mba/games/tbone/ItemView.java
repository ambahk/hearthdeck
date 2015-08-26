package hk.mba.games.tbone;

import hk.mba.games.tbone.elements.UiNode;

/**
 * A very simple view that gives a DomNode view behaviors.
 *
 * @author Albert Kwong
 */
public class ItemView extends View {

	public ItemView(final UiNode<?> el) {
		this.el = el;
	}

	@Override
	public UiNode<?> onRender() {
		return el;
	}
}
