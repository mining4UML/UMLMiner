package com.uniba.mining.logging.extensions;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.XExtensionParser;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.xml.sax.SAXException;

import com.uniba.mining.logging.Logger;

public class XIdentityExtension extends XExtension {
    public static final String KEY_ID = "identity:id";
    public static final XAttributeLiteral ATTR_ID = new XAttributeLiteralImpl(KEY_ID,
            Logger.xIdFactory.createId().toString());
    public static final URI EXTENSION_URI = URI.create("http://www.xes-standard.org/identity.xesext");
    private static final XIdentityExtension xIdentityExtension = parseExtension();
    private static final Collection<XAttribute> definedAttributes = new HashSet<>(Arrays.asList(ATTR_ID));

    private static XIdentityExtension parseExtension() {
        try {
            return new XIdentityExtension(
                    XExtensionParser.instance().parse(EXTENSION_URI));
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        throw new UnsupportedOperationException("Parse extension failed");
    }

    private XIdentityExtension(XExtension xExtension) {
        super(xExtension.getName(), xExtension.getPrefix(), xExtension.getUri());
    }

    public static XIdentityExtension instance() {
        return xIdentityExtension;
    }

    @Override
    public Collection<XAttribute> getDefinedAttributes() {
        return definedAttributes;
    }

}
