/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// Running TestApp: 
// gradle runApp 

package application.java.secured;

import application.java.Config;
import org.hyperledger.fabric.gateway.*;

import java.nio.file.Path;
import java.nio.file.Paths;


public class AppRead {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}
	public static String ID = "1";
	public static String USERNAME = "appUser" + ID;
	public static String assetId = "asset104";
	// helper function for getting connected to the gateway
	public static Gateway connect() throws Exception{
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet/org"+ ID);
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations", "peerOrganizations", "org"+ ID +".example.com", "connection-org"+ ID +".yaml");
		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, USERNAME).networkConfig(networkConfigPath).discovery(true);
		return builder.connect();
	}

	public static void main(String[] args) throws Exception {
		// connect to the network and invoke the smart contract
		try (Gateway gateway = connect()) {
			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract(Config.CHAINCODE_NAME);
			byte[] result;
			System.out.println("\n");
			result = contract.evaluateTransaction("ReadAsset", assetId);
			System.out.println("Evaluate Transaction: ReadAsset, result: " + new String(result));
		}
		catch(Exception e){
			System.err.println(e);
		}

	}
}
