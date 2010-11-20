/*
 * AdTest.java
 *
 * COPYRIGHT  2008
 * THE REGENTS OF THE UNIVERSITY OF MICHIGAN
 * ALL RIGHTS RESERVED
 *
 * PERMISSION IS GRANTED TO USE, COPY, CREATE DERIVATIVE WORKS AND REDISTRIBUTE THIS
 * SOFTWARE AND SUCH DERIVATIVE WORKS FOR NONCOMMERCIAL EDUCATION AND RESEARCH
 * PURPOSES, SO LONG AS NO FEE IS CHARGED, AND SO LONG AS THE COPYRIGHT NOTICE
 * ABOVE, THIS GRANT OF PERMISSION, AND THE DISCLAIMER BELOW APPEAR IN ALL COPIES
 * MADE; AND SO LONG AS THE NAME OF THE UNIVERSITY OF MICHIGAN IS NOT USED IN ANY
 * ADVERTISING OR PUBLICITY PERTAINING TO THE USE OR DISTRIBUTION OF THIS SOFTWARE
 * WITHOUT SPECIFIC, WRITTEN PRIOR AUTHORIZATION.
 *
 * THIS SOFTWARE IS PROVIDED AS IS, WITHOUT REPRESENTATION FROM THE UNIVERSITY OF
 * MICHIGAN AS TO ITS FITNESS FOR ANY PURPOSE, AND WITHOUT WARRANTY BY THE
 * UNIVERSITY OF MICHIGAN OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT
 * LIMITATION THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE. THE REGENTS OF THE UNIVERSITY OF MICHIGAN SHALL NOT BE LIABLE FOR ANY
 * DAMAGES, INCLUDING SPECIAL, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, WITH
 * RESPECT TO ANY CLAIM ARISING OUT OF OR IN CONNECTION WITH THE USE OF THE SOFTWARE,
 * EVEN IF IT HAS BEEN OR IS HEREAFTER ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */
package edu.umich.eecs.tac.props;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import se.sics.isl.transport.BinaryTransportWriter;
import se.sics.isl.transport.BinaryTransportReader;
import static edu.umich.eecs.tac.props.TransportableTestUtils.getBytesForTransportable;
import static edu.umich.eecs.tac.props.TransportableTestUtils.readFromBytes;

import java.text.ParseException;

/**
 * @author Patrick Jordan
 */
public class AdTest {

	@Test
	public void testGenericAd() throws ParseException {
		BinaryTransportWriter writer = new BinaryTransportWriter();
		BinaryTransportReader reader = new BinaryTransportReader();
		reader.setContext(new AAInfo().createContext());

		Ad ad = new Ad();

		assertNotNull(ad);
		assertTrue(ad.isGeneric());
		assertEquals(ad.toString(), "(Ad generic:true product:null)");

		byte[] buffer = getBytesForTransportable(writer, ad);
		Ad received = readFromBytes(reader, buffer, "Ad");

		assertEquals(ad.isGeneric(), received.isGeneric());
		assertEquals(ad.getProduct(), received.getProduct());
		assertEquals(ad, received);
	}

	@Test
	public void testSpecificAd() throws ParseException {
		BinaryTransportWriter writer = new BinaryTransportWriter();
		BinaryTransportReader reader = new BinaryTransportReader();
		reader.setContext(new AAInfo().createContext());

		Ad ad = new Ad();
		ad.setProduct(new Product("m", "c"));

		assertNotNull(ad);
		assertFalse(ad.isGeneric());
		assertEquals(ad.toString(),
				"(Ad generic:false product:(Product (m,c)))");

		byte[] buffer = getBytesForTransportable(writer, ad);
		Ad received = readFromBytes(reader, buffer, "Ad");

		assertEquals(ad.isGeneric(), received.isGeneric());
		assertEquals(ad.getProduct(), received.getProduct());
		assertEquals(ad, received);

		ad = new Ad(new Product("m", "c"));
		assertNotNull(ad);
		assertFalse(ad.isGeneric());
		assertFalse(ad.equals(null));
		assertEquals(ad.toString(),
				"(Ad generic:false product:(Product (m,c)))");

		buffer = getBytesForTransportable(writer, ad);
		received = readFromBytes(reader, buffer, "Ad");

		assertEquals(ad.isGeneric(), received.isGeneric());
		assertEquals(ad.getProduct(), received.getProduct());
		assertEquals(ad, received);

		Ad emptyAd = new Ad();
		assertFalse(emptyAd.equals(ad));
		assertFalse(ad.equals(emptyAd));
	}

	@Test
	public void testHashCode() {
		Ad ad = new Ad(new Product("m", "c"));
		assertEquals(ad.hashCode(), ad.getProduct().hashCode());

		ad = new Ad();
		assertEquals(ad.hashCode(), 0);
	}

	@Test
	public void testEquals() {
		Ad ad = new Ad();
		assertTrue(ad.equals(ad));
		assertFalse(ad.equals(new Product()));
		assertFalse(ad.equals(null));

		ad.setProduct(new Product("m", "c"));
		assertTrue(ad.equals(ad));
		assertFalse(ad.equals(new Ad()));
	}
}
