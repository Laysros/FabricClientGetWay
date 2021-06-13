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

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


public class AppCreate {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}
	public static String ID = "2";
	public static String USERNAME = "appUser" + ID;
	// helper function for getting connected to the gateway
	public static Gateway connect() throws Exception{
		Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet/org"+ ID));
		// load a CCP
		Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations", "peerOrganizations", "org"+ ID +".example.com", "connection-org"+ ID +".yaml");
		Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, USERNAME).networkConfig(networkConfigPath).discovery(true);
		return builder.connect();
	}

	public static void main(String[] args) throws Exception {
		String salt =Long.toHexString(Double.doubleToLongBits(Math.random()));
		String tradeId = Long.toHexString(Double.doubleToLongBits(Math.random()));
		byte[] result;
		String assetId = "asset1011";
		BloodPrivate bloodPrivate = new BloodPrivate(assetId, "AB", 500, "Mr.A", salt);
		BloodPublic bloodPublic = new BloodPublic(assetId, tradeId);
		String asset_properties = new Gson().toJson(bloodPrivate);
		String asset_price;
		Map<String, byte[]> transientMap = new HashMap<>();
		Transaction t;
		try (Gateway gateway = connect()) {
			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract(Config.CHAINCODE_NAME);
			transientMap.put("asset_properties", asset_properties.getBytes(StandardCharsets.UTF_8));
			t = contract.createTransaction("CreateAsset");
			t.setTransient(transientMap);
			t.submit(assetId, "Asset id is for sale");
			System.out.println("---------------------------------------");
			System.out.println("\n");
			result = contract.evaluateTransaction("GetAssetPrivateProperties", assetId);
			System.out.println("Private: " + new String(result, StandardCharsets.UTF_8));

			System.out.println("\n");
			result = contract.evaluateTransaction("ReadAsset", assetId);
			System.out.println("Public: " + new String(result, StandardCharsets.UTF_8));
		}
		catch(Exception e){
			System.err.println(e);
		}

	}
}
