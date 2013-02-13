package forge.card.cardFactory;


import com.esotericsoftware.minlog.Log;
import forge.AllZone;
import forge.AllZoneUtil;
import forge.Card;
import forge.CardList;
import forge.CardListFilter;
import forge.CardListUtil;
import forge.CardUtil;
import forge.Command;
import forge.Constant;
import forge.Player;
import forge.PlayerZone;
import forge.card.spellability.Ability;
import forge.card.spellability.Cost;
import forge.card.spellability.Spell;
import forge.card.spellability.SpellAbility;
import forge.card.spellability.Spell_Permanent;
import forge.card.spellability.Target;
import forge.gui.GuiUtils;
import forge.gui.input.Input;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * <p>CardFactory_Auras class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
class CardFactory_Auras {

    /**
     * <p>shouldCycle.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a int.
     */
    public final static int shouldCycle(Card c) {
        ArrayList<String> a = c.getKeyword();
        for (int i = 0; i < a.size(); i++) {
            if (a.get(i).toString().startsWith("Cycling")) {
                return i;
            }
        }
        return -1;
    }

    /*   Auras which used enPump have been converted to AF_Attach
    /**
     * <p>shouldEnchant.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a int.
     */    /*
    public static int shouldEnchant(Card c) {
        ArrayList<String> a = c.getKeyword();
        for (int i = 0; i < a.size(); i++)
            if (a.get(i).toString().startsWith("enPump")) return i;

        return -1;
    }    */

    /*   Auras which used enControlCreature have been converted to AF_Attach
    /**
     * <p>shouldControl.</p>
     *
     * @param c a {@link forge.Card} object.
     * @return a int.
     */    /*
    public static int shouldControl(Card c) {
        ArrayList<String> a = c.getKeyword();
        for (int i = 0; i < a.size(); i++)
            if (a.get(i).toString().startsWith("enControlCreature")) return i;

        return -1;
    }    */

    /**
     * <p>getCard.</p>
     *
     * @param card a {@link forge.Card} object.
     * @param cardName a {@link java.lang.String} object.
     * @param owner a {@link forge.Player} object.
     * @return a {@link forge.Card} object.
     */
    public static Card getCard(final Card card, final String cardName, final Player owner) {

        /* This command was used by the generic aura code which is no longer needed
    	Command standardUnenchant = new Command() {
            private static final long serialVersionUID = 3938247133551483568L;

            public void execute() {
                if (card.isEnchanting()) {
                    Card crd = card.getEnchanting().get(0);
                    card.unEnchantCard(crd);
                }
            }
        };    */


        // *****************************************************************
        // Enchant Lands:              *************************************
        // Enchant land you control:   *************************************
        // *****************************************************************

        //*************** START *********** START **************************
        if (cardName.equals("Convincing Mirage") || cardName.equals("Phantasmal Terrain")
                || cardName.equals("Spreading Seas") || cardName.equals("Evil Presence")
                || cardName.equals("Lingering Mirage") || cardName.equals("Sea's Claim")
                || cardName.equals("Tainted Well")) {

            final String[] newType = new String[1];
            final SpellAbility spell = new Spell(card) {

                private static final long serialVersionUID = 53941812202244498L;

                @Override
                public boolean canPlayAI() {

                    if (!super.canPlayAI()) {
                        return false;
                    }

                    if (card.getName().equals("Spreading Seas")
                            || card.getName().equals("Lingering Mirage")
                            || card.getName().equals("Sea's Claim")
                            || card.getName().equals("Phantasmal Terrain")) {
                        newType[0] = "Island";
                    } else if (card.getName().equals("Evil Presence")
                            || card.getName().equals("Tainted Well")) {
                        newType[0] = "Swamp";
                    } else if (card.getName().equals("Convincing Mirage")
                            || card.getName().equals("Phantasmal Terrain")) {
                        String[] landTypes = new String[]{"Plains", "Island", "Swamp", "Mountain", "Forest"};
                        HashMap<String, Integer> humanLandCount = new HashMap<String, Integer>();
                        CardList humanlands = AllZoneUtil.getPlayerLandsInPlay(AllZone.getHumanPlayer());

                        for (int i = 0; i < landTypes.length; i++) {
                            humanLandCount.put(landTypes[i], 0);
                        }

                        for (Card c : humanlands) {
                            for (String singleType : c.getType()) {
                                if (CardUtil.isABasicLandType(singleType)) {
                                    humanLandCount.put(singleType, humanLandCount.get(singleType) + 1);
                                }
                            }
                        }

                        int minAt = 0;
                        int minVal = Integer.MAX_VALUE;
                        for (int i = 0; i < landTypes.length; i++) {
                            if (getTargetCard().isType(landTypes[i])) {
                                continue;
                            }

                            if (humanLandCount.get(landTypes[i]) < minVal) {
                                minVal = humanLandCount.get(landTypes[i]);
                                minAt = i;
                            }
                        }

                        newType[0] = landTypes[minAt];
                    }
                    CardList list = AllZoneUtil.getPlayerLandsInPlay(AllZone.getHumanPlayer());
                    list = list.getNotType(newType[0]); // Don't enchant lands that already have the type
                    if (list.isEmpty()) {
                        return false;
                    }
                    setTargetCard(list.get(0));
                    return true;
                }//canPlayAI()

                @Override
                public void resolve() {
                    if (card.getName().equals("Spreading Seas")
                            || card.getName().equals("Lingering Mirage")
                            || card.getName().equals("Sea's Claim")) {
                        newType[0] = "Island";
                    } else if (card.getName().equals("Evil Presence")
                            || card.getName().equals("Tainted Well")) {
                        newType[0] = "Swamp";
                    } else if (card.getName().equals("Convincing Mirage")
                            || card.getName().equals("Phantasmal Terrain")) {
                        //Only query player, AI will have decided already.
                        if (card.getController().isHuman()) {
                            newType[0] = GuiUtils.getChoice("Select land type.", "Plains", "Island", "Swamp", "Mountain", "Forest");
                        }
                    }
                    AllZone.getGameAction().moveToPlay(card);

                    Card c = getTargetCard();

                    if (AllZoneUtil.isCardInPlay(c) 
                            && CardFactoryUtil.canTarget(card, c)) {
                        card.enchantCard(c);
                    }

                }//resolve()
            };//SpellAbility


            spell.setDescription("");
            card.addSpellAbility(spell);

            // Need to set the spell description for Lingering Mirage since it has cycling ability.
            if (card.getName().equals("Lingering Mirage")) {
                spell.setDescription("Enchanted land is an Island.");
            }

            Command onEnchant = new Command() {

                private static final long serialVersionUID = 3528675112863241126L;

                public void execute() {
                    if (card.isEnchanting()) {
                        Card crd = card.getEnchanting().get(0);
                        ArrayList<Card> seas = crd.getEnchantedBy();
                        int count = 0;
                        for (int i = 0; i < seas.size(); i++) {
                            if (seas.get(i).getName().equals(card.getName())) {
                                count = count + 1;
                            }
                        }
                        if (count == 1) {
                            crd.removeType("Swamp");
                            crd.removeType("Forest");
                            crd.removeType("Island");
                            crd.removeType("Plains");
                            crd.removeType("Mountain");
                            crd.removeType("Locus");
                            crd.removeType("Lair");

                            crd.addType(newType[0]);
                        } else {
                            Card Other_Seas = null;
                            for (int i = 0; i < seas.size(); i++) {
                                if (seas.get(i) != card) {
                                    Other_Seas = seas.get(i);
                                }
                            }
                            SpellAbility[] Abilities = Other_Seas.getSpellAbility();
                            for (int i = 0; i < Abilities.length; i++) {
                                card.addSpellAbility(Abilities[i]);
                            }
                        }
                    }
                }//execute()
            };//Command

            Command onUnEnchant = new Command() {
                private static final long serialVersionUID = -202144631191180334L;

                public void execute() {
                    if (card.isEnchanting()) {
                        Card crd = card.getEnchanting().get(0);
                        ArrayList<Card> seas = crd.getEnchantedBy();
                        int count = 0;
                        for (int i = 0; i < seas.size(); i++) {
                            if (seas.get(i).getName().equals(card.getName())) {
                                count = count + 1;
                            }
                        }
                        if (count == 1) {
                            crd.removeType(newType[0]);
                            crd.removeType("Land");
                            crd.removeType("Basic");
                            crd.removeType("Snow");
                            crd.removeType("Legendary");
                            SpellAbility[] Card_Abilities = crd.getSpellAbility();
                            for (int i = 0; i < Card_Abilities.length; i++) {
                                if (Card_Abilities[i].isIntrinsic()) {
                                    crd.removeSpellAbility(Card_Abilities[i]);
                                }
                            }
                            Card c = AllZone.getCardFactory().copyCard(crd);
                            ArrayList<String> types = c.getType();
                            SpellAbility[] Abilities = card.getSpellAbility();
                            for (int i = 0; i < types.size(); i++) {
                                crd.addType(types.get(i));
                            }
                            for (int i = 0; i < Abilities.length; i++) {
                                crd.addSpellAbility(Abilities[i]);
                            }
                        }
                    }
                }//execute()
            };//Command

            Command onLeavesPlay = new Command() {

                private static final long serialVersionUID = -45433022112460839L;

                public void execute() {
                    if (card.isEnchanting()) {
                        Card crd = card.getEnchanting().get(0);
                        card.unEnchantCard(crd);
                    }
                }
            };

            card.addEnchantCommand(onEnchant);
            card.addUnEnchantCommand(onUnEnchant);
            card.addLeavesPlayCommand(onLeavesPlay);

            Input runtime = new Input() {

                private static final long serialVersionUID = -62372711146079880L;

                @Override
                public void showMessage() {
                    CardList land = AllZoneUtil.getLandsInPlay();
                    stopSetNext(CardFactoryUtil.input_targetSpecific(spell, land, "Select target land", true,
                            false));
                }
            };
            spell.setBeforePayMana(runtime);
        }//*************** END ************ END **************************


        // *****************************************************************
        // Enchant artifacts:   ********************************************
        // *****************************************************************


        // *****************************************************************
        // Enchant creatures:   ********************************************
        // *****************************************************************

        //*************** START *********** START **************************
        else if (cardName.equals("Earthbind")) {
            Cost cost = new Cost(card.getManaCost(), cardName, false);
            Target tgt = new Target(card, "C");
            final SpellAbility spell = new Spell_Permanent(card, cost, tgt) {

                private static final long serialVersionUID = 142389375702113977L;

                @Override
                public boolean canPlayAI() {
                    CardList list = AllZoneUtil.getCreaturesInPlay(AllZone.getHumanPlayer());
                    list = list.getKeyword("Flying");
                    if (list.isEmpty()) {
                        return false;
                    }

                    CardListFilter f = new CardListFilter() {
                        public final boolean addCard(Card c) {
                            return c.getNetDefense() - c.getDamage() <= 2;
                        }
                    };
                    if (!list.filter(f).isEmpty()) {
                        list = list.filter(f);
                    }
                    CardListUtil.sortAttack(list);

                    for (int i = 0; i < list.size(); i++) {
                        if (CardFactoryUtil.canTarget(card, list.get(i))) {
                            setTargetCard(list.get(i));
                            return super.canPlayAI();
                        }
                    }
                    return false;
                }//canPlayAI()

                @Override
                public void resolve() {
                    AllZone.getGameAction().moveToPlay(card);

                    Card c = getTargetCard();

                    if (AllZoneUtil.isCardInPlay(c)
                            && CardFactoryUtil.canTarget(card, c)) {
                        card.enchantCard(c);
                        Log.debug("Enchanted: " + getTargetCard());
                    }
                }//resolve()
            };//SpellAbility

            card.addSpellAbility(spell);

            final boolean[] badTarget = {true};
            Command onEnchant = new Command() {

                private static final long serialVersionUID = -5302506578307993978L;

                public void execute() {
                    if (card.isEnchanting()) {
                        Card crd = card.getEnchanting().get(0);
                        if (crd.hasKeyword("Flying")) {
                            badTarget[0] = false;
                            crd.addDamage(2, card);
                            crd.removeIntrinsicKeyword("Flying");
                            crd.removeExtrinsicKeyword("Flying");
                        } else {
                            badTarget[0] = true;
                        }
                    }
                }//execute()
            };//Command

            Command onUnEnchant = new Command() {

                private static final long serialVersionUID = -6908757692588823391L;

                public void execute() {
                    if (card.isEnchanting()
                            && !badTarget[0]) {
                        Card crd = card.getEnchanting().get(0);
                        crd.addIntrinsicKeyword("Flying");
                    }
                }//execute()
            };//Command

            Command onLeavesPlay = new Command() {

                private static final long serialVersionUID = -7833240882415702940L;

                public void execute() {
                    if (card.isEnchanting()) {
                        Card crd = card.getEnchanting().get(0);
                        card.unEnchantCard(crd);
                    }
                }
            };

            card.addEnchantCommand(onEnchant);
            card.addUnEnchantCommand(onUnEnchant);
            card.addLeavesPlayCommand(onLeavesPlay);
        }//*************** END ************ END **************************


        //*************** START *********** START **************************
        else if (cardName.equals("Guilty Conscience")) {
            Cost cost = new Cost(card.getManaCost(), cardName, false);
            Target tgt = new Target(card, "C");
            final SpellAbility spell = new Spell_Permanent(card, cost, tgt) {

                private static final long serialVersionUID = 1169151960692309514L;

                @Override
                public boolean canPlayAI() {

                    CardList stuffy = AllZoneUtil.getPlayerCardsInPlay(AllZone.getComputerPlayer(), "Stuffy Doll");

                    if (stuffy.size() > 0) {
                        setTargetCard(stuffy.get(0));
                        return true;
                    } else {
                        CardList list = AllZoneUtil.getCreaturesInPlay(AllZone.getHumanPlayer());

                        if (list.isEmpty()) {
                            return false;
                        }

                        //else
                        CardListUtil.sortAttack(list);
                        CardListUtil.sortFlying(list);

                        for (int i = 0; i < list.size(); i++) {
                            if (CardFactoryUtil.canTarget(card, list.get(i))
                                    && (list.get(i).getNetAttack() >= list.get(i).getNetDefense())
                                    && list.get(i).getNetAttack() >= 3) {
                                setTargetCard(list.get(i));
                                return super.canPlayAI();
                            }
                        }
                    }
                    return false;

                }//canPlayAI()

                @Override
                public void resolve() {
                    AllZone.getGameAction().moveToPlay(card);

                    Card c = getTargetCard();

                    if (AllZoneUtil.isCardInPlay(c)
                            && CardFactoryUtil.canTarget(card, c)) {
                        card.enchantCard(c);
                    }
                }//resolve()
            };//SpellAbility

            card.addSpellAbility(spell);
        }//*************** END ************ END **************************


        //*************** START *********** START **************************
        else if (cardName.equals("Animate Dead") || cardName.equals("Dance of the Dead")) {
            final Card[] targetC = new Card[1];
            // need to override what happens when this is cast.
            final Spell_Permanent animate = new Spell_Permanent(card) {
                private static final long serialVersionUID = 7126615291288065344L;

                public CardList getCreturesInGrave() {
                    // This includes creatures Animate Dead can't enchant once in play.
                    // The human may try to Animate them, the AI will not.
                    CardList cList = AllZoneUtil.getCardsInGraveyard();
                    cList = cList.getType("Creature");
                    return cList;
                }

                public boolean canPlay() {
                    return super.canPlay() && getCreturesInGrave().size() != 0;
                }

                @Override
                public boolean canPlayAI() {
                    CardList cList = getCreturesInGrave();
                    // AI will only target something that will stick in play.
                    cList = cList.filter(new CardListFilter() {
                        public final boolean addCard(Card crd) {
                            return CardFactoryUtil.canTarget(card, crd) && !CardFactoryUtil.hasProtectionFrom(card, crd);
                        }
                    });
                    if (cList.size() == 0) {
                        return false;
                    }

                    Card c = CardFactoryUtil.AI_getBestCreature(cList);

                    setTargetCard(c);
                    boolean playable = 2 < c.getNetAttack() && 2 < c.getNetDefense() && super.canPlayAI();
                    return playable;
                }//canPlayAI

                @Override
                public void resolve() {
                    targetC[0] = getTargetCard();
                    super.resolve();
                }

            };//addSpellAbility

            // Target AbCost and Restriction are set here to get this working as expected
            Target tgt = new Target(card, "Select a creature in a graveyard", "Creature".split(","));
            tgt.setZone(Constant.Zone.Graveyard);
            animate.setTarget(tgt);

            Cost cost = new Cost("1 B", cardName, false);
            animate.setPayCosts(cost);

            animate.getRestrictions().setZone(Constant.Zone.Hand);

            final Ability attach = new Ability(card, "0") {
                private static final long serialVersionUID = 222308932796127795L;

                @Override
                public void resolve() {
                    PlayerZone play = AllZone.getZone(Constant.Zone.Battlefield, card.getController());

                    // Animate Dead got destroyed before its ability resolved
                    if (!AllZoneUtil.isCardInZone(play, card)) {
                        return;
                    }

                    Card animated = targetC[0];
                    PlayerZone grave = AllZone.getZone(animated);

                    if (!grave.is(Constant.Zone.Graveyard)) {
                        // Animated Creature got removed before ability resolved
                        AllZone.getGameAction().sacrifice(card);
                        return;
                    }

                    // Bring creature onto the battlefield under your control (should trigger etb Abilities)
                    animated.setController(card.getController());
                    AllZone.getGameAction().moveToPlay(animated, card.getController());
                    if (cardName.equals("Dance of the Dead")) {
                        animated.tap();
                    }
                    card.enchantCard(animated);    // Attach before Targeting so detach Command will trigger

                    if (CardFactoryUtil.hasProtectionFrom(card, animated)) {
                        // Animated a creature with protection
                        AllZone.getGameAction().sacrifice(card);
                        return;
                    }

                    // Everything worked out perfectly.
                }
            };//Ability

            final Command attachCmd = new Command() {
                private static final long serialVersionUID = 3595188622377350327L;

                public void execute() {
                    AllZone.getStack().addSimultaneousStackEntry(attach);

                }
            };

            final Ability detach = new Ability(card, "0") {

                @Override
                public void resolve() {
                    Card c = targetC[0];

                    PlayerZone play = AllZone.getZone(Constant.Zone.Battlefield, card.getController());

                    if (AllZoneUtil.isCardInZone(play, c)) {
                        AllZone.getGameAction().sacrifice(c);
                    }
                }
            };//Detach

            final Command detachCmd = new Command() {
                private static final long serialVersionUID = 2425333033834543422L;

                public void execute() {
                    Card c = targetC[0];

                    PlayerZone play = AllZone.getZone(Constant.Zone.Battlefield, card.getController());

                    if (AllZoneUtil.isCardInZone(play, c)) {
                        AllZone.getStack().addSimultaneousStackEntry(detach);
                    }

                }
            };
            
            card.addSpellAbility(animate);

            attach.setStackDescription("Attaching " + cardName + " to creature in graveyard.");
            card.addComesIntoPlayCommand(attachCmd);
            detach.setStackDescription(cardName + " left play. Sacrificing creature if still around.");
            card.addLeavesPlayCommand(detachCmd);
            card.addUnEnchantCommand(detachCmd);
        }//*************** END ************ END **************************

        /*    Converted these auras from hard coded to AF_Attach + StaticAbility
        //*************** START *********** START **************************
        else if (cardName.equals("Entangling Vines") || cardName.equals("Glimmerdust Nap") ||
                cardName.equals("Melancholy") || cardName.equals("Mystic Restraints") ||
                cardName.equals("Roots") || cardName.equals("Thirst")) {

            final SpellAbility spell = new Spell(card) {
                private static final long serialVersionUID = 843412563175285562L;

                @Override
                public boolean canPlayAI() {

                    if (!super.canPlayAI()) return false;

                    CardList list = AllZoneUtil.getCreaturesInPlay(AllZone.getHumanPlayer());    // Target human creature
                    list = list.filter(new CardListFilter() {
                        public boolean addCard(Card c) {
                            return CardFactoryUtil.canTarget(card, c) &&
                                    !c.hasKeyword("CARDNAME doesn't untap during your untap step.");
                        }
                    });

                    if (card.hasKeyword("Enchant tapped creature")) {
                        list = list.filter(new CardListFilter() {
                            public boolean addCard(Card c) {
                                return c.isTapped();
                            }
                        });
                    }

                    if (card.hasKeyword("Enchant creature without flying")) {
                        list = list.filter(new CardListFilter() {
                            public boolean addCard(Card c) {
                                return !c.hasKeyword("Flying");
                            }
                        });
                    }

                    if (list.isEmpty()) {
                        return false;
                    } else {
                        CardListUtil.sortAttack(list);
                        if (!card.hasKeyword("Enchant creature without flying")) {
                            CardListUtil.sortFlying(list);
                        }
                        setTargetCard(list.get(0));
                    }
                    return true;
                }//canPlayAI()

                @Override
                public void resolve() {
                    AllZone.getGameAction().moveToPlay(card);

                    Card c = getTargetCard();

                    if (AllZoneUtil.isCardInPlay(c) && CardFactoryUtil.canTarget(card, c)) {
                        if (card.hasKeyword("When CARDNAME enters the battlefield, tap enchanted creature.")) {
                            c.tap();
                        }
                        card.enchantCard(c);
                    }
                }//resolve()
            };//SpellAbility

            spell.setDescription("");
            card.addSpellAbility(spell);

            Command onEnchant = new Command() {
                private static final long serialVersionUID = -8694692627290877222L;

                public void execute() {
                    if (card.isEnchanting()) {
                        Card crd = card.getEnchanting().get(0);
                        if (!crd.hasKeyword("CARDNAME doesn't untap during your untap step."))
                            crd.addExtrinsicKeyword("CARDNAME doesn't untap during your untap step.");
                    }
                }//execute()
            };//Command

            Command onUnEnchant = new Command() {
                private static final long serialVersionUID = -8271629765371049921L;

                public void execute() {
                    if (card.isEnchanting()) {
                        Card crd = card.getEnchanting().get(0);
                        crd.removeExtrinsicKeyword("CARDNAME doesn't untap during your untap step.");
                    }

                }//execute()
            };//Command

            Command onLeavesPlay = new Command() {
                private static final long serialVersionUID = -8694692627290877222L;

                public void execute() {
                    if (card.isEnchanting()) {
                        Card crd = card.getEnchanting().get(0);
                        card.unEnchantCard(crd);
                    }
                }
            };

            Input runtime = new Input() {
                private static final long serialVersionUID = 5974269912215230241L;

                @Override
                public void showMessage() {
                    CardList creatures = AllZoneUtil.getCreaturesInPlay();
                    creatures = creatures.filter(AllZoneUtil.getCanTargetFilter(card));

                    String instruction = "Select target creature";

                    if (card.hasKeyword("Enchant tapped creature")) {
                        instruction = "Select target tapped creature";
                        creatures = creatures.filter(AllZoneUtil.tapped);
                    }

                    if (card.hasKeyword("Enchant creature without flying")) {
                        instruction = "Select target creature without flying";
                        creatures = creatures.filter(new CardListFilter() {
                            public boolean addCard(Card c) {
                                return !c.hasKeyword("Flying");
                            }
                        });
                    }

                    stopSetNext(CardFactoryUtil.input_targetSpecific(spell, creatures, instruction, true, false));
                }
            };
            card.addEnchantCommand(onEnchant);
            card.addUnEnchantCommand(onUnEnchant);
            card.addLeavesPlayCommand(onLeavesPlay);

            spell.setBeforePayMana(runtime);
        }//*************** END ************ END **************************    */


        //*************** START *********** START **************************
        else if (CardFactory.hasKeyword(card, "enchant") != -1) {
            int n = CardFactory.hasKeyword(card, "enchant");
            if (n != -1) {
                String parse = card.getKeyword().get(n).toString();
                String k[] = parse.split(":");

                SpellAbility sa = card.getSpellAbility()[0];
                sa.setIsMultiKicker(true);
                sa.setMultiKickerManaCost(k[1]);
            }
        }


        /*    Auras which used the generic aura have been converted to AF_Attach
        ///////////////////////////////////////////////////////////////////
        ////
        //// CAUTION: Keep this last in the if else if block for cardnames
        ////
        ///////////////////////////////////////////////////////////////////

        ////////////////////DRF test generic aura
        //*************** START *********** START **************************
        else if (isAuraType(card, "Land")     || isAuraType(card, "Creature")    ||
                 isAuraType(card, "Artifact") || isAuraType(card, "Enchantment") ||
                 isAuraType(card, "Wall")) {

            final String type = getAuraType(card);
            final boolean curse = isCurseAura(card);
            final boolean youControl = isTypeYouControl(card);
            final boolean oppControl = isTypeOppControl(card);
            if ("" == type) {
                Log.error("Problem in generic Aura code - type is null");
            }
            final SpellAbility spell = new Spell(card) {
                private static final long serialVersionUID = 4191777361540717307L;

                @Override
                public boolean canPlayAI() {
                    Player player;
                    if (curse || oppControl) {
                        player = AllZone.getHumanPlayer();
                    } else {
                        player = AllZone.getComputerPlayer();
                    }
                    CardList list = AllZoneUtil.getPlayerTypeInPlay(player, type);
                    list = list.filter(AllZoneUtil.getCanTargetFilter(card));

                    if (list.isEmpty()) return false;

                    // Enchant a random Aura magnet if one is in play.
                    // Else enchant a random unenchanted card if one exists.
                    // Enchant a previously enchanted card if no unenchanted cards exist.

                    if (!curse) {
                        CardList magnets = list.getEnchantMagnets();

                        if (!magnets.isEmpty()) {
                            list = magnets;

                        } else {
                            CardList notEnchanted = list.filter(AllZoneUtil.unenchanted);
                            if (!notEnchanted.isEmpty()) {
                                list = notEnchanted;
                            }
                        }
                    } else {
                        CardList notEnchanted = list.filter(AllZoneUtil.unenchanted);
                        if (!notEnchanted.isEmpty()) {
                            list = notEnchanted;
                        }
                    }
                    // We do not want the AI to always enchant the same card.

                    list.shuffle();
                    setTargetCard(list.get(0));
                    return super.canPlayAI();

                }//canPlayAI()

                @Override
                public void resolve() {
                    AllZone.getGameAction().moveToPlay(card);

                    Card c = getTargetCard();
                    if (AllZoneUtil.isCardInPlay(c)
                            && CardFactoryUtil.canTarget(card, c)) {
                        card.enchantCard(c);
                    }
                }//resolve()
            };//SpellAbility
            spell.setDescription("");
            card.addSpellAbility(spell);
            card.addLeavesPlayCommand(standardUnenchant);

            Input runtime = new Input() {
                private static final long serialVersionUID = -7100800261954421849L;

                @Override
                public void showMessage() {
                    // We will now use a list name other than "land", ugh!
                    // CardList land = AllZoneUtil.getTypeInPlay(type);

                    StringBuilder sbTitle = new StringBuilder();
                    sbTitle.append("Select target ").append(type.toLowerCase());
                    if (youControl) {
                        sbTitle.append(" you control");
                    } else if (oppControl) {
                        sbTitle.append(" an opponent controls");
                    }
                    CardList auraCandidates = new CardList();
                    if (youControl) {
                        auraCandidates = AllZoneUtil.getPlayerTypeInPlay(card.getController(), type);
                    } else if (oppControl) {
                        auraCandidates = AllZoneUtil.getPlayerTypeInPlay(card.getController().getOpponent(), type);
                    } else {
                        auraCandidates = AllZoneUtil.getTypeInPlay(type);
                    }
                    stopSetNext(CardFactoryUtil.input_targetSpecific(spell, auraCandidates,
                            sbTitle.toString(), true, false));
                }
            };
            spell.setBeforePayMana(runtime);
        }//*************** END ************ END **************************

        ///////////////////////////////////////////////////////////////////
        ////
        //// CAUTION: Keep the above code block last in the if else if block
        ////
        ///////////////////////////////////////////////////////////////////
        ////////////////////DRF test generic aura    */


        /*   Auras which used enPump have been converted to AF_Attach
         *   This section is for cards which add a P/T boost
         *   and/or keywords to the enchanted creature
         */    /*
        if (shouldEnchant(card) != -1) {
            int n = shouldEnchant(card);
            if (n != -1) {
                String parse = card.getKeyword().get(n).toString();
                card.removeIntrinsicKeyword(parse);

                String k[] = parse.split(":");
                String keywordsUnsplit = "";
                String extrinsicKeywords[] = {"none"};    // for enchantments with no keywords to add

                final String spDesc[] = {"none"};
                final String stDesc[] = {"none"};
                StringBuilder sbD = new StringBuilder();
                StringBuilder sbSD = new StringBuilder();
                final boolean curse[] = {false};

                curse[0] = k[0].contains("Curse");

                int Power = 0;
                int Tough = 0;

                sbD.append("Enchanted creature ");

                String ptk[] = k[1].split("/");

                if (ptk.length == 1)     // keywords in first cell
                {
                    keywordsUnsplit = ptk[0];
                } else    // parse the power/toughness boosts in first two cells
                {
                    sbD.append("gets ");
                    sbD.append(ptk[0].trim());
                    sbD.append("/");
                    sbD.append(ptk[1].trim());

                    for (int i = 0; i < 2; i++) {
                        if (ptk[i].matches("[\\+\\-][0-9]+")) ptk[i] = ptk[i].replace("+", "");
                    }
                    Power = Integer.parseInt(ptk[0].trim());
                    Tough = Integer.parseInt(ptk[1].trim());

                    if (ptk.length > 2)     // keywords in third cell
                    {
                        keywordsUnsplit = ptk[2];
                        sbD.append(" and ");
                    }
                }

                if (keywordsUnsplit.length() > 0)    // then there is at least one extrinsic keyword to assign
                {
                    sbD.append("has ");

                    String tempKwds[] = keywordsUnsplit.split("&");
                    extrinsicKeywords = new String[tempKwds.length];

                    for (int i = 0; i < tempKwds.length; i++) {
                        extrinsicKeywords[i] = tempKwds[i].trim();

                        sbD.append(extrinsicKeywords[i].toLowerCase());
                        if (i < tempKwds.length - 2) {
                            sbD.append(", ");
                        }
                        if (i == tempKwds.length - 2) {
                            sbD.append(" and ");
                        }
                    }
                }
                sbD.append(".");
                spDesc[0] = sbD.toString();

                sbSD.append(cardName);
                sbSD.append(" - ");
                sbSD.append("enchants target creature.");
                stDesc[0] = sbSD.toString();

                if (k.length > 2) {
                    spDesc[0] = k[2].trim();
                }    // Use the spell and stack descriptions included
                if (k.length > 3) {
                    stDesc[0] = k[3].trim();
                }    // with the keyword if they are present.

                card.clearFirstSpell();    // A "generic" Spell is added before this
                if (!curse[0]) {
                    card.addFirstSpellAbility(CardFactoryUtil.enPump_Enchant(card, Power, Tough, extrinsicKeywords, spDesc, stDesc));
                } else {
                    card.addFirstSpellAbility(CardFactoryUtil.enPumpCurse_Enchant(card, Power, Tough, extrinsicKeywords, spDesc, stDesc));
                }
                card.addEnchantCommand(CardFactoryUtil.enPump_onEnchant(card, Power, Tough, extrinsicKeywords, spDesc, stDesc));
                card.addUnEnchantCommand(CardFactoryUtil.enPump_unEnchant(card, Power, Tough, extrinsicKeywords, spDesc, stDesc));
                card.addLeavesPlayCommand(CardFactoryUtil.enPump_LeavesPlay(card, Power, Tough, extrinsicKeywords, spDesc, stDesc));

                card.setSVar("PlayMain1", "TRUE");
            }
        }// enPump[Curse]    */
        

        // Need to keep the return card; statement below.
        return card;
    }

    
    
    
    /*    Auras which used the generic aura have been converted to AF_Attach
    //checks if an aura is a given type based on: Enchant <type> in cards.txt
    /**
     * <p>isAuraType.</p>
     *
     * @param aura a {@link forge.Card} object.
     * @param type a {@link java.lang.String} object.
     * @return a boolean.
     */    /*
    private static boolean isAuraType(final Card aura, final String type) {
        ArrayList<String> keywords = aura.getKeyword();
        for (String keyword : keywords) {
            if (keyword.startsWith("Enchant " + type)) {
                return true;
            }
        }
        return false;
    }    */

    /*    Auras which used the generic aura have been converted to AF_Attach
    //gets the type of aura based on Enchant <type> in card.txt
    /**
     * <p>getAuraType.</p>
     *
     * @param aura a {@link forge.Card} object.
     * @return a {@link java.lang.String} object.
     */    /*
    private static String getAuraType(final Card aura) {
        ArrayList<String> keywords = aura.getKeyword();
        for (String keyword : keywords) {
            if (keyword.startsWith("Enchant ")) {
                StringTokenizer st = new StringTokenizer(keyword);
                st.nextToken(); //this should be "Enchant"
                return st.nextToken();  //should be "land", "artifact", etc
            }
        }
        return "";
    }    */

    /*    Auras which used the generic aura have been converted to AF_Attach
    //checks if an aura is a curse based on Enchant <type> [Curse] in cards.txt
    //Curse just means computer will target human's stuff with this
    /**
     * <p>isCurseAura.</p>
     *
     * @param aura a {@link forge.Card} object.
     * @return a boolean.
     */    /*
    private static boolean isCurseAura(final Card aura) {
        ArrayList<String> keywords = aura.getKeyword();
        for (String keyword : keywords) {
            if (keyword.startsWith("Enchant ")) {
                if (keyword.endsWith("Curse")) return true;
            }
        }
        return false;
    }    */

    /*    Auras which used the generic aura have been converted to AF_Attach
    //checks if the aura can only target the controller's cards
    /**
     * <p>isTypeYouControl.</p>
     *
     * @param aura a {@link forge.Card} object.
     * @return a boolean.
     */    /*
    private static boolean isTypeYouControl(final Card aura) {
        ArrayList<String> keywords = aura.getKeyword();
        for (String keyword : keywords) {
            if (keyword.startsWith("Enchant ")) {
                if (keyword.endsWith("you control")) return true;
            }
        }
        return false;
    }    */

    /*    Auras which used the generic aura have been converted to AF_Attach
    //checks if the aura can only target the opponent's cards
    /**
     * <p>isTypeOppControl.</p>
     *
     * @param aura a {@link forge.Card} object.
     * @return a boolean.
     */    /*
    private static boolean isTypeOppControl(final Card aura) {
        ArrayList<String> keywords = aura.getKeyword();
        for (String keyword : keywords) {
            if (keyword.startsWith("Enchant ")) {
                if (keyword.contains("an opponent controls")) return true;
            }
        }
        return false;
    }    */

}
