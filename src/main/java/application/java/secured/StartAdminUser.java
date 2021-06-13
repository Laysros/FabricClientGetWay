/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// Running TestApp: 
// gradle runApp 

package application.java.secured;


import org.apache.commons.io.FileUtils;
import org.hyperledger.fabric.gateway.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


public class StartAdminUser {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	// helper function for getting connected to the gateway
	public static Gateway connect() throws Exception{
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet");
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "appUser2").networkConfig(networkConfigPath).discovery(true);
		return builder.connect();
	}

	public static void main(String[] args) throws Exception {
		// enrolls the admin and registers the user
		FileUtils.deleteDirectory(new File("wallet/org1"));
		FileUtils.deleteDirectory(new File("wallet/org2"));

		try {
			EnrollAdmin.main(1);
			EnrollAdmin.main(2);
			RegisterUser.main(1);
			RegisterUser.main(2);
		} catch (Exception e) {
			System.err.println(e);
		}

	}
}
