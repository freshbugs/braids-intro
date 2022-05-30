import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.applet.Applet;

public class Braid extends Applet {
    private ImagePanel imagePanel;

    TextField braidWordField = new TextField(10);

    public void init() {
        setLayout(new BorderLayout(3,3)); //3 pixels of padding
        add("North", braidWordField);

        imagePanel = new  ImagePanel(this);
        add("Center", imagePanel);

        braidWordField.addActionListener(imagePanel);
    }

    public String getAppletInfo() {
        return "A simple braid drawing program.";
    }
}

class ImagePanel extends Canvas implements ActionListener {
    Applet	applet;
    Image       ditherImage;
    Image       offScreenBuffer;

    int         strands = 5;
    StringBuffer braidWord = new StringBuffer("1");
    int strandSpacing = 0;
    int strandWidth = 0;
    int leftStrand = 0;

    public ImagePanel(Applet app) {
        applet = app;
    }

    Image makeDitherImage(int w) {
	int h = w/2;
	int pix[] = new int[w * h];
	int index = 0;
	for (int y = 0; y < h; y++) {
	    for (int x = 0; x < w; x++) {
		int red = (255 * (x+ h-y)) / (w+h);
                int color = (255 << 24) | (red << 16);
		pix[index++] = color;
	    }
	}
	return applet.createImage(new MemoryImageSource(w, h, pix, 0, w));
    }

    public void actionPerformed (ActionEvent e) {
        braidWord = new StringBuffer(e.getActionCommand());
        repaint();
    }

    // override update to do double buffering
    public void update(Graphics g) {
        Graphics gr; 
        // Will hold the graphics context from the offScreenBuffer.
        // We need to make sure we keep our offscreen buffer the same size
        // as the graphics context we're working with.
         if (offScreenBuffer==null ||
                    (! (offScreenBuffer.getWidth(this) == getBounds().width
                    &&offScreenBuffer.getHeight(this) == getBounds().height)))
            {
            offScreenBuffer 
                = this.createImage(getBounds().width, getBounds().height);
            }
    
        // We need to use our buffer Image as a Graphics object:
        gr = offScreenBuffer.getGraphics();
    
        paint(gr); // Passes our off-screen buffer to our paint method, which,
                   // unsuspecting,
                   // paints on it just as it would on the Graphics
                   // passed by the browser or applet viewer.
        g.drawImage(offScreenBuffer, 0, 0, this);
                   // And now we transfer the info in the buffer onto the
                   // graphics context we got from the browser
                   // in one smooth motion.
    }

    public void paint(Graphics g) {
	Rectangle r = getBounds();
        
        int s;
        int generator;
        int exponent;
        int x,y;
        int xinc;
        int index;

//Toolkit.getDefaultToolkit().beep(); //for debugging the flickering

        g.setColor(Color.darkGray);
        g.clearRect(0, 0, r.width, r.height);
        g.drawRect(0, 0, r.width - 1, r.height - 1);

        while(braidWord.length() * r.width/strands < r.height) {
            braidWord.append('1');
        }
        strandSpacing = r.height / braidWord.length();
        if(strandWidth != strandSpacing / 3) {
            strandWidth = strandSpacing / 3;
            leftStrand = (r.width-((strands-1)*strandSpacing+strandWidth))/2;
            ditherImage = makeDitherImage(strandWidth);
        }

        for(index = 0; index < braidWord.length(); index++) {
            char ch = braidWord.charAt(index);
            if('a' <= ch && ch <= 'z') {
                generator = ch - 'a';
                exponent = 1;
            } else if('A' <= ch && ch <= 'Z') {
                generator = ch - 'A';
                exponent = -1;
            } else {
                generator = strands + 1;
                exponent = 1;
            }
            if(generator >= strands-1) {
                generator = strands + 1;
            }

            s = ((exponent==1) ? 0 : strands-1);
            for( ; (0 <= s) && (s < strands); s += exponent) {
                x = leftStrand + s * strandSpacing;
                y = index * strandSpacing;
                if(generator == s-1) xinc = -1;
                else if(generator == s) xinc = 1;
                else xinc = 0;
                
                for(int i = 0; i < strandSpacing; i++) {
                    g.drawImage(ditherImage, x, y, this);
                    y++;
                    x += xinc;
                }
            }
	}
    }
}
