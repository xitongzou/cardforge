package forge;

import forge.Card;
import forge.card.trigger.TriggerHandler;
import forge.error.ErrorViewer;
import forge.properties.NewConstants;

import java.io.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.code.jyield.YieldUtils;

import net.slightlymagic.braids.util.generator.FindNonDirectoriesSkipDotDirectoriesGenerator;
import net.slightlymagic.braids.util.generator.GeneratorFunctions;

import forge.gui.MultiPhaseProgressMonitorWithETA;


/**
 * <p>ReadCard class.</p>
 *
 * @author Forge
 * @version $Id: $
 */
public class ReadCard implements Runnable, NewConstants {
    private BufferedReader in;
    Map<String,Card> mapToFill;
	private File cardsfolder;
    
    private File zipFile;

    /**
     * <p>getCards.</p>
     *
     * @deprected This isn't being used by anything, so it has been commented out; 
     * future revisions may delete this method.
     * 
     * @return a {@link java.util.ArrayList} object.
     */
    //public ArrayList<Card> getCards() {
    //    return new ArrayList<Card>(allCards);
    //}

    /**
     * <p>Constructor for ReadCard.</p>
     * 
     * @deprected This isn't being used by anything, so it has been commented out; 
     * future revisions may delete this method.
     *
     * @param filename a {@link java.lang.String} object.
     */
    //public ReadCard(String filename) {
    //    this(new File(filename));
    //}

    /**
     * <p>Constructor for ReadCard.</p>
     *
     * @param cardsfolder a {@link java.io.File} object.
     * 
     * @param mapToFill maps card names to Card instances; this is where we place the cards once read
     * 
     */
    public ReadCard(File cardsfolder, Map<String, Card> mapToFill) {
    	if (mapToFill == null) {
    		throw new NullPointerException("mapToFill must not be null.");
    	}
    	this.mapToFill = mapToFill;
    	
        if (!cardsfolder.exists())
            throw new RuntimeException("ReadCard : constructor error -- file not found -- filename is "
                    + cardsfolder.getAbsolutePath());

        if (!cardsfolder.isDirectory())
            throw new RuntimeException("ReadCard : constructor error -- not a directory -- "
                    + cardsfolder.getAbsolutePath());
        zipFile = new File(cardsfolder, CARDSFOLDER + ".zip");

        this.cardsfolder = cardsfolder;
    }//ReadCard()

	/**
     * <p>run.</p>
     * @since 1.0.15
     */
    public void run() {
        Card c = null;

        if (zipFile.exists()) {
            try {
                ZipFile zip = new ZipFile(zipFile);
                ZipEntry entry;
                
                int zipSize = zip.size();
                
                MultiPhaseProgressMonitorWithETA monitor = 
                	new MultiPhaseProgressMonitorWithETA("Forge - Loading card database from ZIP", 
                			1, zipSize, 1.0f);
                
                Enumeration<? extends ZipEntry> e = zip.entries();
                while (e.hasMoreElements()) {

                	entry = (ZipEntry) e.nextElement();
                    
                    if (entry.isDirectory() || !entry.getName().endsWith(".txt")) {
                    	monitor.incrementUnitsCompletedThisPhase(1L);
                        continue;
                    }
                    
                    in = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
                    c = new Card();
                    loadCard(c);
                    
                    mapToFill.put(c.getName(), c);
                    
                    in.close();
                	monitor.incrementUnitsCompletedThisPhase(1L);
                }
                
                monitor.getDialog().dispose();
                
            } catch (Exception e) {

            }

        } else {

        	FindNonDirectoriesSkipDotDirectoriesGenerator findNonDirsGen = new FindNonDirectoriesSkipDotDirectoriesGenerator(cardsfolder);
        	long fileCount = GeneratorFunctions.estimateSize(findNonDirsGen);
        	
            MultiPhaseProgressMonitorWithETA monitor = 
            	new MultiPhaseProgressMonitorWithETA("Forge - Loading card database from files", 
            			1, fileCount, 1.0f);
            
            for (File cardTxtFile : YieldUtils.toIterable(findNonDirsGen)) {
				if (!cardTxtFile.getName().endsWith(".txt")) {
	            	monitor.incrementUnitsCompletedThisPhase(1L);
                    continue;
				}

                try {
                    in = new BufferedReader(new FileReader(cardTxtFile));
                } catch (Exception ex) {
                    ErrorViewer.showError(ex, "File \"%s\" exception", cardTxtFile.getAbsolutePath());
                    throw new RuntimeException("ReadCard : run error -- file exception -- filename is "
                            + cardTxtFile.getPath(),
                            ex);
                }

                c = new Card();
                loadCard(c);

                mapToFill.put(c.getName(), c);

            	monitor.incrementUnitsCompletedThisPhase(1L);

                try {
                    in.close();
                } catch (IOException ex) {
                    ErrorViewer.showError(ex, "File \"%s\" exception", cardTxtFile.getAbsolutePath());
                    throw new RuntimeException("ReadCard : run error -- file exception -- filename is "
                            + cardTxtFile.getPath(),
                            ex);
                }

            } //endfor

            monitor.getDialog().dispose();

        } //endif

    }//run()

    /**
     * <p>addTypes.</p>
     *
     * @param c a {@link forge.Card} object.
     * @param types a {@link java.lang.String} object.
     */
    private void addTypes(Card c, String types) {
        StringTokenizer tok = new StringTokenizer(types);
        while (tok.hasMoreTokens())
            c.addType(tok.nextToken());
    }

    /**
     * <p>readLine.</p>
     *
     * @return a {@link java.lang.String} object.
     * @param in a {@link java.io.BufferedReader} object.
     */
    private String readLine() {
        //makes the checked exception, into an unchecked runtime exception
        try {
            String s = in.readLine();
            if (s != null) s = s.trim();
            return s;
        } catch (Exception ex) {
            ErrorViewer.showError(ex);
            throw new RuntimeException("ReadCard : readLine(Card) error", ex);
        }
    }//readLine(Card)


    /**
     * <p>loadCard.</p>
     *
     * @param c a {@link forge.Card} object.
     */
    private void loadCard(Card c) {
        String s = readLine();
        while (!s.equals("End")) {
            if (s.startsWith("#")) {
                //no need to do anything, this indicates a comment line
            } else if (s.startsWith("Name:")) {
                String t = s.substring(5);

                //if (Constant.Runtime.DevMode[0])
                //	System.out.println("ReadCard: " + s);

                if (mapToFill.containsKey(t)) {
                    System.out.println("ReadCard:run() error - duplicate card name: " + t);
                    throw new RuntimeException("ReadCard:run() error - duplicate card name: " + t);
                } else
                    c.setName(t);
            } else if (s.startsWith("ManaCost:")) {
                String t = s.substring(9);
                //System.out.println(s);
                if (!t.equals("no cost"))
                    c.setManaCost(t);
            } else if (s.startsWith("Types:"))
                addTypes(c, s.substring(6));

            else if (s.startsWith("Text:")) {
                String t = s.substring(5);
                // if (!t.equals("no text"));
                if (t.equals("no text")) t = ("");
                c.setText(t);
            } else if (s.startsWith("PT:")) {
                String t = s.substring(3);
                String pt[] = t.split("/");
                int att = pt[0].contains("*") ? 0 : Integer.parseInt(pt[0]);
                int def = pt[1].contains("*") ? 0 : Integer.parseInt(pt[1]);
                c.setBaseAttackString(pt[0]);
                c.setBaseDefenseString(pt[1]);
                c.setBaseAttack(att);
                c.setBaseDefense(def);
            } else if (s.startsWith("Loyalty:")) {
                String splitStr[] = s.split(":");
                int loyal = Integer.parseInt(splitStr[1]);
                c.setBaseLoyalty(loyal);
            } else if (s.startsWith("K:")) {
                String t = s.substring(2);
                c.addIntrinsicKeyword(t);
            } else if (s.startsWith("SVar:")) {
                String t[] = s.split(":", 3);
                c.setSVar(t[1], t[2]);
            } else if (s.startsWith("A:")) {
                String t = s.substring(2);
                c.addIntrinsicAbility(t);
            } else if (s.startsWith("T:")) {
                String t = s.substring(2);
                c.addTrigger(TriggerHandler.parseTrigger(t, c));
            } else if (s.startsWith("S:")) {
                String t = s.substring(2);
                c.addStaticAbilityString(t);
            } else if (s.startsWith("SetInfo:")) {
                String t = s.substring(8);
                c.addSet(new SetInfo(t));
            }

            s = readLine();
        } // while !End
    }
}
