package chapter1;

import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ListAlgorithms
{
	/**
	 * Print out the set entries, indented, one per line, with the name of the set
	 * unindented appearing on the first line.
	 * 
	 * @param setName the name of the set being printed
	 * @param algorithms the set of algorithms associated with the given name
	 */
	public static void printSet(
			String setName,
			Set	   algorithms)
	{
		System.out.println(setName + ":");

		if (algorithms.isEmpty())
		{
			System.out.println("            None available.");
		}
		else
		{
			Iterator	it = algorithms.iterator();

			while (it.hasNext())
			{
				String	name = (String)it.next();

				System.out.println("            " + name);
			}
		}
	}

	/**
	 * List the available algorithm names for ciphers, key agreement, macs,
	 * message digests and signatures.
	 */
	public static void main(
			String[]    args)
	{
		Provider[]	providers = Security.getProviders();
		Set			ciphers = new HashSet();
		Set			keyAgreements = new HashSet();
		Set			macs = new HashSet();
		Set			messageDigests = new HashSet();
		Set			signatures = new HashSet();
		String		proveedor_filtrado = "SUN";

		System.out.println("Proveedores\n============");
		for (int i = 0; i != providers.length; i++)
		{
			
			System.out.println(providers[i].getName());

			if (!proveedor_filtrado.isEmpty() && providers[i].getName().equals(proveedor_filtrado))
			{
				Iterator  it = providers[i].keySet().iterator();

				while (it.hasNext())
				{
					String	entry = (String)it.next();

					if (entry.startsWith("Alg.Alias."))
					{
						entry = entry.substring("Alg.Alias.".length());
					}

					if (entry.startsWith("Cipher."))
					{
						ciphers.add(entry.substring("Cipher.".length()));
					}
					else if (entry.startsWith("KeyAgreement."))
					{
						keyAgreements.add(entry.substring("KeyAgreement.".length()));
					}
					else if (entry.startsWith("Mac."))
					{
						macs.add(entry.substring("Mac.".length()));
					}
					else if (entry.startsWith("MessageDigest."))
					{
						messageDigests.add(entry.substring("MessageDigest.".length()));
					}
					else if (entry.startsWith("Signature."))
					{
						signatures.add(entry.substring("Signature.".length()));
					}
				}
			}
		}
		
		
		printSet("Ciphers", ciphers);
		System.out.println(ciphers.size());
		printSet("KeyAgreeents", keyAgreements);
		System.out.println(keyAgreements.size());
		printSet("Macs", macs);
		System.out.println(macs.size());
		printSet("MessageDigests", messageDigests);
		System.out.println(messageDigests.size());
		printSet("Signatures", signatures);
		System.out.println(signatures.size());
	}
}
