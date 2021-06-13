/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// Running TestApp: 
// gradle runApp 

package application.java.blood;

import application.java.Config;
import application.java.model.BloodPrivate;
import application.java.model.BloodPublic;
import application.java.model.blood.ProposalPrivate;
import application.java.model.blood.ProposalPublic;
import com.google.gson.Gson;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Peer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class CreateProposal {

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}
	public static String ID = "1";
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
		String proposalId = "proposal1001901099";
		ProposalPrivate proposalPrivate = new ProposalPrivate(proposalId, "A+", salt);
		String asset_properties = new Gson().toJson(proposalPrivate);
		Map<String, byte[]> transientMap = new HashMap<>();
		Transaction t;
		try (Gateway gateway = connect()) {
			// get the network and contract
			Network network = gateway.getNetwork("mychannel");
			Contract contract = network.getContract(Config.CHAINCODE_NAME);
			transientMap.put("asset_properties", asset_properties.getBytes(StandardCharsets.UTF_8));
			t = contract.createTransaction("CreateProposal");
			t.setTransient(transientMap);
			t.submit(proposalId, "40",  "Request blood for emergency");
			System.out.println("---------------------------------------");
			System.out.println("\n");
			result = contract.evaluateTransaction("GetAssetPrivateProperties", proposalId);
			System.out.println("Private: " + new String(result, StandardCharsets.UTF_8));
			System.out.println("\n");
			result = contract.evaluateTransaction("ReadProposal", proposalId);
			System.out.println("Public: " + new String(result, StandardCharsets.UTF_8));

			System.out.println("---------------------------------------");
			t = contract.createTransaction("SubmitProposal");
			Collection<Peer> peers = network.getChannel().getPeers();
			for(Peer p:peers){
				System.out.println("Peer:" + p.getName() + "-URL:" + p.getUrl());
			}
			t.setEndorsingPeers(peers);
			transientMap = new HashMap<>();
			transientMap.put("asset_properties", asset_properties.getBytes(StandardCharsets.UTF_8));
			t.setTransient(transientMap);
			t.submit(proposalId, "Org2MSP");
			System.out.println("Submitted");
			result = contract.evaluateTransaction("ReadAsset", proposalId);
			System.out.println("Read Asset: " + new String(result, StandardCharsets.UTF_8));





		}
		catch(Exception e){
			System.err.println(e);
		}

	}
}
