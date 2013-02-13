package forge;

import java.util.ArrayList;

import net.slightlymagic.braids.util.generator.GeneratorFunctions;
import net.slightlymagic.braids.util.lambda.Lambda1;

import com.google.code.jyield.Generator;

/**
 * <p>CardFilter class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class CardFilter {


    /**
     * Filter a sequence (iterable) of cards to a list of equal or smaller size
     * whose names contain the given substring.
     * 
     * We perform the substring search without sensitivity to case.
     *
     * @param toBeFiltered an {@link java.lang.Iterable} of Card instances
     * @param substring a {@link java.lang.String} object.
     * @return a {@link forge.CardList} object.
     */
    public CardList cardListNameFilter(Iterable<Card> toBeFiltered, String substring) 
    {
        String s;

        CardList listFilter = new CardList();
        for (Card card : toBeFiltered) {
            s = card.getName().toLowerCase();

            if (s.indexOf(substring.toLowerCase()) >= 0) {
                listFilter.add(card);

            }

        }

        return listFilter;
    }

    /**
     * <p>CardListTextFilter.</p>
     *
	 * TODO: style: rename this method so it starts with a lowercase letter
	 * 
     * @param all a {@link forge.CardList} object.
     * @param name a {@link java.lang.String} object.
     * @return a {@link forge.CardList} object.
     */
    public CardList CardListTextFilter(CardList all, String name) {
        Card CardName;
        String s;
        s = "";
        CardList listFilter = new CardList();
        for (int i = 0; i < all.size(); i++) {
            CardName = all.getCard(i);
            s = CardName.getText().toLowerCase();

            if (s.indexOf(name.toLowerCase()) >= 0) {
                listFilter.add(CardName);

            }

        }

        return listFilter;
    }


    /**
     * <p>CardListColorFilter.</p>
     *
	 * TODO: style: rename this method so it starts with a lowercase letter
	 * 
     * @param all a {@link forge.CardList} object.
     * @param name a {@link java.lang.String} object.
     * @return a {@link forge.CardList} object.
     */
    public CardList CardListColorFilter(CardList all, String name) {
        Card CardName = new Card();
        CardList listFilter = new CardList();

        if (name == "black") {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardUtil.getColors(CardName).contains(Constant.Color.Black) == false) {
                    listFilter.add(CardName);
                }

            }
        }

        if (name == "blue") {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardUtil.getColors(CardName).contains(Constant.Color.Blue) == false) {
                    listFilter.add(CardName);
                }

            }
        }

        if (name == "green") {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardUtil.getColors(CardName).contains(Constant.Color.Green) == false) {
                    listFilter.add(CardName);
                }

            }
        }

        if (name == "red") {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardUtil.getColors(CardName).contains(Constant.Color.Red) == false) {
                    listFilter.add(CardName);
                }

            }
        }

        if (name == "white") {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardUtil.getColors(CardName).contains(Constant.Color.White) == false) {
                    listFilter.add(CardName);
                }

            }
        }

        if (name.equals("colorless")) {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardUtil.getColors(CardName).contains(Constant.Color.Colorless) == false) {
                    listFilter.add(CardName);
                }

            }
        }

        return listFilter;
    }

    /**
     * <p>CardListTypeFilter.</p>
     *
	 * TODO: style: rename this method so it starts with a lowercase letter
	 * 
     * @param all a {@link forge.CardList} object.
     * @param name a {@link java.lang.String} object.
     * @return a {@link forge.CardList} object.
     */
    public CardList CardListTypeFilter(CardList all, String name) {
        Card CardName = new Card();
        CardList listFilter = new CardList();

        if (name == "artifact") {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardName.isArtifact() == false) {
                    listFilter.add(CardName);
                }

            }
        }

        if (name == "creature") {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardName.isCreature() == false) {
                    listFilter.add(CardName);
                }

            }
        }

        if (name == "enchantment") {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardName.isEnchantment() == false) {
                    listFilter.add(CardName);
                }

            }
        }

        if (name == "instant") {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardName.isInstant() == false) {
                    listFilter.add(CardName);
                }

            }
        }

        if (name == "land") {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardName.isLand() == false) {
                    listFilter.add(CardName);
                }

            }
        }

        if (name == "planeswalker") {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardName.isPlaneswalker() == false) {
                    listFilter.add(CardName);
                }

            }
        }

        if (name.equals("sorcery")) {
            for (int i = 0; i < all.size(); i++) {
                CardName = all.getCard(i);
                if (CardName.isSorcery() == false) {
                    listFilter.add(CardName);
                }

            }
        }

        return listFilter;
    }

	public static Generator<Card> getRarity(Generator<Card> inputGenerator, final String rarity) {
		Lambda1<Boolean,Card> predicate = new Lambda1<Boolean,Card>() {
			public Boolean apply(Card c) {
	            // TODO spin off Mythic from Rare when the time comes
	            String r = c.getSVar("Rarity");
	            return r.equals(rarity) ||
	                    rarity.equals(Constant.Rarity.Rare) && r.equals(Constant.Rarity.Mythic);
			}
		};
		
		return GeneratorFunctions.filterGenerator(predicate, inputGenerator);
	}

	/**
	 * Filter an iterable sequence of Cards; note this is a static method
	 * that is very similar to the non-static one.
	 * 
	 * @param iterable  the sequence of cards to examine
	 * 
	 * @param filt  determines which cards are present in the resulting list
	 * 
	 * @return a list of Cards that meet the filtering criteria; may be empty,
	 * but never null
	 */
	public static CardList filter(Iterable<Card> iterable, CardListFilter filt) {
	    CardList result = new CardList();
	    for (Card card : iterable)
	        if (filt.addCard(card)) {
	        	result.add(card);
	        }
	
	    return result;
	}

	/**
	 * Filter a Generator of Cards based on their colors; this does not cause
	 * the generator to be evaluated, but rather defers the filtering to when
	 * the result's generate method is called (e.g., by YieldUtils.toIterable).
	 * 
	 * @param inputGenerator  the sequence to filter
	 * 
	 * @param cardColor
	 *            a {@link java.lang.String} object; "Multicolor" is also
	 *            accepted.
	 * 
	 * @return a new Generator with the filter in place
	 */
    public static Generator<Card> getColor(Generator<Card> inputGenerator, final String cardColor) 
    {
		Lambda1<Boolean,Card> predicate = new Lambda1<Boolean,Card>() {
			public Boolean apply(Card c) {
				if (cardColor.equals("Multicolor") && c.getColor().size() > 1) {
					return true;
				}
				else if (c.isColor(cardColor) && c.getColor().size() == 1) {
					return true;
				}

				return false;
			}
		};
		
		return GeneratorFunctions.filterGenerator(predicate, inputGenerator);
    }//getColor()

    /**
	 * <p>Get any cards that exist in the passed in sets list.</p>
	 *
	 * @param sets a {@link java.util.ArrayList} object.
	 * @return a {@link forge.CardList} object.
	 */
	public static Generator<Card> getSets(Generator<Card> inputGenerator, 
			final ArrayList<String> sets) 
	{
		Lambda1<Boolean,Card> predicate = new Lambda1<Boolean,Card>() {
			public Boolean apply(Card c) {
	            for (SetInfo set : c.getSets()) {
	                if (sets.contains(set.toString())) {
	                	return true;
	                }
	            }
	            
	            return false;
			}
		};
		
		return GeneratorFunctions.filterGenerator(predicate, inputGenerator);
		
	}//getSets(Generator,ArrayList)


}
