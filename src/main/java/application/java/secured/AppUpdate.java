/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// Running TestApp: 
// gradle runApp 

package application.java.secured;

import application.java.Config;
import application.java.model.BloodPrivate;
import application.java.model.BloodPublic;
import com.google.gson.Gson;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Peer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class AppUpdate {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	// helper function for getting connected to the gateway
	public static Gateway connectORG(int org) throws Exception{
		// Load a file system based wallet for managing identities.
		Path walletPath = Paths.get("wallet/org" + org);
		Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations", "peerOrganizations", "org"+ org +".example.com", "connection-org"+ org +".yaml");

		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "appUser"+ org).networkConfig(networkConfigPath).discovery(true);
		return builder.connect();
	}

	public static void main(String[] args) throws Exception {
		byte[] result;
		String assetId = "asset1031";
		Transaction t;
		try (Gateway gateway = connectORG(2)) {
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract(Config.CHAINCODE_NAME);
			System.out.println("---------------------------------------");
			try {
				t = contract.createTransaction("ChangePublicDescription");
				result = t.submit(assetId,"the asset is not x for sell xyx.6 df sdfdsf");
				System.out.println("Changed?" + new String(result, StandardCharsets.UTF_8));
			} catch (ContractException e) {
				System.out.println(e);
			}
			result = contract.evaluateTransaction("ReadAsset", assetId);
			System.out.println("Read Asset: " + new String(result, StandardCharsets.UTF_8));
		}catch(Exception e){
			System.err.println(e);
		}

	}
}
