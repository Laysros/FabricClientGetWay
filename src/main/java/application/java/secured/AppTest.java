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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class AppTest {

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
		String salt =Long.toHexString(Double.doubleToLongBits(Math.random()));
		String tradeId = Long.toHexString(Double.doubleToLongBits(Math.random()));
		byte[] result;
		String assetId = "asset103120";
		BloodPrivate bloodPrivate = new BloodPrivate(assetId, "AB", 500, "Mr.A", salt);
		BloodPublic bloodPublic = new BloodPublic(assetId, tradeId);
		String asset_properties = new Gson().toJson(bloodPrivate);
		String asset_price;
		Map<String, byte[]> transientMap = new HashMap<>();
		Transaction t;
		try (Gateway gateway = connectORG(1)) {
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
			System.out.println("Original: " + result);
			System.out.println("String: " + result.toString());
			BloodPrivate b = new Gson().fromJson(new String(result, StandardCharsets.UTF_8), BloodPrivate.class);
			System.out.println("PRIVATE : " + new Gson().toJson(b));

			System.out.println("\n");
			result = contract.evaluateTransaction("ReadAsset", assetId);
			System.out.println("Public: " + new String(result, StandardCharsets.UTF_8));


			System.out.println("---------------------------------------");
			t = contract.createTransaction("AgreeToSell");
			transientMap = new HashMap<>();
			bloodPublic.setPrice(100);
			asset_price = new Gson().toJson(bloodPublic);
			transientMap.put("asset_price", asset_price.getBytes(StandardCharsets.UTF_8));
			t.setTransient(transientMap);
			t.submit(assetId);
			result = contract.evaluateTransaction("GetAssetSalesPrice", assetId);
			System.out.println("PUBLIC Get Asset Sales Price: " + new String(result, StandardCharsets.UTF_8));
		}catch(Exception e){
			System.err.println(e);
		}


		try (Gateway gateway = connectORG(2)) {
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract(Config.CHAINCODE_NAME);
			System.out.println("---------------------------------------");
			try {
				t = contract.createTransaction("VerifyAssetProperties");
				transientMap = new HashMap<>();
				transientMap.put("asset_properties", asset_properties.getBytes(StandardCharsets.UTF_8));
				t.setTransient(transientMap);
				result = t.evaluate(assetId);
				System.out.println("Verify AssetProperties: " + new String(result, StandardCharsets.UTF_8));
			} catch (ContractException e) {
				System.out.println(e);
			}
			try {
				t = contract.createTransaction("ChangePublicDescription");
				result = t.submit(assetId,"the worst asset");
				System.out.println("Changed?" + new String(result, StandardCharsets.UTF_8));
			} catch (ContractException e) {
				System.out.println(e);
			}


			System.out.println("---------------------------------------");
			t = contract.createTransaction("AgreeToBuy");
			transientMap = new HashMap<>();
			bloodPublic.setPrice(100);
			asset_price = new Gson().toJson(bloodPublic);
			System.out.println("PUBLIC ASSET_PRICE:" + asset_price);
			transientMap.put("asset_price", asset_price.getBytes(StandardCharsets.UTF_8));
			t.setTransient(transientMap);
			t.submit(assetId);

			result = contract.evaluateTransaction("GetAssetBidPrice", assetId);
			System.out.println("PUBLIC Get Asset Bid Price: " + new String(result, StandardCharsets.UTF_8));
		}catch(Exception e){
			System.err.println(e);
		}

		try (Gateway gateway = connectORG(1)) {
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract(Config.CHAINCODE_NAME);
			bloodPublic.setPrice(100);
			asset_price = new Gson().toJson(bloodPublic);
			try{
				System.out.println("---------------------------------------");
				t = contract.createTransaction("TransferAsset");
				Collection<Peer> peers = network.getChannel().getPeers();
				for(Peer p:peers){
					System.out.println("Peer:" + p.getName() + "-URL:" + p.getUrl());
				}
				t.setEndorsingPeers(peers);
				transientMap = new HashMap<>();
				transientMap.put("asset_properties", asset_properties.getBytes(StandardCharsets.UTF_8));
				System.out.println("PUBLIC:" + asset_price);
				transientMap.put("asset_price", asset_price.getBytes(StandardCharsets.UTF_8));
				t.setTransient(transientMap);
				t.submit(assetId, "Org2MSP");
				System.out.println("Done Transfer");
			}catch (ContractException e){
				System.out.println(e);
			}
			result = contract.evaluateTransaction("ReadAsset", assetId);
			System.out.println("Read Asset: " + new String(result, StandardCharsets.UTF_8));

		}
		catch(Exception e){
			System.err.println(e);
		}

		try (Gateway gateway = connectORG(2)) {
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract(Config.CHAINCODE_NAME);
			System.out.println("---------------------------------------");
			try {
				t = contract.createTransaction("ChangePublicDescription");
				result = t.submit(assetId,"the asset is not for sell x.");
				System.out.println("Changed?" + new String(result, StandardCharsets.UTF_8));
			} catch (ContractException e) {
				System.out.println(e);
			}
			result = contract.evaluateTransaction("ReadAsset", assetId);
			System.out.println("Read Asset: " + new String(result, StandardCharsets.UTF_8));
		}catch(Exception e){
			System.err.println(e);
		}
		try (Gateway gateway = connectORG(1)) {
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
			System.out.println("Original: " + result);
			System.out.println("String: " + result.toString());
			BloodPrivate b = new Gson().fromJson(new String(result, StandardCharsets.UTF_8), BloodPrivate.class);
			System.out.println("PRIVATE : " + new Gson().toJson(b));

			System.out.println("\n");
			result = contract.evaluateTransaction("ReadAsset", assetId);
			System.out.println("Public: " + new String(result, StandardCharsets.UTF_8));


			System.out.println("---------------------------------------");
			t = contract.createTransaction("AgreeToSell");
			transientMap = new HashMap<>();
			bloodPublic.setPrice(100);
			asset_price = new Gson().toJson(bloodPublic);
			transientMap.put("asset_price", asset_price.getBytes(StandardCharsets.UTF_8));
			t.setTransient(transientMap);
			t.submit(assetId);
			result = contract.evaluateTransaction("GetAssetSalesPrice", assetId);
			System.out.println("PUBLIC Get Asset Sales Price: " + new String(result, StandardCharsets.UTF_8));
		}catch(Exception e){
			System.err.println(e);
		}
	}
}
