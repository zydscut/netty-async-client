package com.thomas.serialize;

import java.util.UUID;

public enum Name {
	Adam,
	Alex,
	Alexander,
	Alan,
	Albert,
	Andrew,
	Andy,
	Anthony,
	Austin,
	Ben,
	Bill,
	Bob,
	Brandon,
	Brant,
	Brian,
	Bruce,
	Carl,
	Cary,
	Charles,
	Chris,
	Colin,
	Daniel,
	Dennis,
	Derek,
	Donald,
	Douglas,
	David,
	Denny,
	Edward,
	Edwin,
	Elvis,
	Eric,
	Evan,
	Francis,
	Frank,
	Franklin,
	Fred,
	Gaby,
	Gary,
	Gavin,
	George,
	Glen,
	Harrison,
	Hugo,
	Hunk,
	Howard,
	Henry,
	Ivan,
	Jack,
	Jackson,
	Jacob,
	James,
	Jason,
	Jeffery,
	Jerome,
	Jerry,
	Jesse,
	Jim,
	Jimmy,
	Joe,
	John,
	Johnny,
	Joseph,
	Justin,
	Keith,
	Ken,
	Kevin,
	Lance,
	Larry,
	Lee,
	Leo,
	Leonard,
	Mark,
	Marks,
	Mars,
	Martin,
	Matthew,
	Michael,
	Mike,
	Neil,
	Nicholas,
	Oliver,
	Oscar,
	Paul,
	Peter,
	Philip,
	Randy,
	Rex,
	Richard,
	Richie,
	Robert,
	Robin,
	Robinson,
	Rock,
	Roger,
	Roy,
	Sam,
	Sammy,
	Samuel,
	Scott,
	Sean,
	Shawn,
	Sidney,
	Simon,
	Solomon,
	Spark,
	Stanley,
	Steven,
	Terry,
	Tommy,
	Tom,
	Thomas,
	Tony,
	Tyler,
	Van,
	Vincent,
	Warren,
	Wesley,
	William;
	
	public static Name valueOf(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            throw new IndexOutOfBoundsException("Invalid ordinal");
        }
        return values()[ordinal];
    }
	
	private static int length = -1;
	
	public static int length() {
		if(length < 0) {
			length = values().length;
		}
		return length;
	}
	
	public static String getRandomName() {
		int ordinal = Math.abs((int)(UUID.randomUUID().getLeastSignificantBits())%(Name.length()));
		String name = String.valueOf(Name.valueOf(ordinal));
		return name;
	}
}
