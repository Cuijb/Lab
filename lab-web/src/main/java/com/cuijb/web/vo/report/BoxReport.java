package com.cuijb.web.vo.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoxReport {
	private String id;
	private List<BootReport> boots = new ArrayList<>();

	private String bcType = null;
	private long bcBT = 0L;
	private Map<String, Continuity> bc = new HashMap<>();

	private String netType = null;
	private long netBT = 0L;
	private Map<String, Continuity> network = new HashMap<>();

	private String ldpcType = null;
	private long ldpcBT = 0L;
	private Continuity ldpc;

	private List<ErrorMsg> errors = new ArrayList<>();

	public BoxReport(String id) {
		this.id = id;
	}

	public BootReport boot(long starTime) {
		if (boots.isEmpty()) {
			BootReport boot = new BootReport();
			boot.refresh(starTime);
			boots.add(boot);
		}
		return boots.get(boots.size() - 1);
	}

	public void add(BootReport bootReport) {
		if (null == bootReport) {
			return;
		}
		boots.add(bootReport);
	}

	public void fixed(long endTime) {
		// boots
		List<BootReport> filtedBoots = new ArrayList<>();
		for (BootReport boot : boots) {
			boot.fixed();
			if (!boot.empty()) {
				filtedBoots.add(boot);
			}
		}
		boots = filtedBoots;

		// bc
		if (null != bcType) {
			Continuity bcCon = bc.get(bcType);
			if (null == bcCon) {
				bcCon = new Continuity();
				bc.put(bcType, bcCon);
			}
			bcCon.plus(bcBT, endTime);
		}

		// network
		if (null != netType) {
			Continuity netCon = network.get(netType);
			if (null == netCon) {
				netCon = new Continuity();
				network.put(netType, netCon);
			}
			netCon.plus(netBT, endTime);
		}

		if (null != ldpc) {
			ldpc.plus(ldpcBT, endTime);
		}
	}

	public void bc(String type, long time) {
		if (bcBT <= 0) {
			bcType = type;
			bcBT = time;
			return;
		}

		if (!bcType.equals(type)) {
			Continuity continuity = bc.get(bcType);
			if (null == continuity) {
				continuity = new Continuity();
				bc.put(bcType, continuity);
			}
			continuity.plus(bcBT, time);

			bcType = type;
			bcBT = time;
		}
	}

	public void network(String type, long time) {
		if (netBT <= 0) {
			netType = type;
			netBT = time;
			return;
		}

		if (!netType.equals(type)) {
			Continuity continuity = network.get(netType);
			if (null == continuity) {
				continuity = new Continuity();
				network.put(netType, continuity);
			}
			continuity.plus(netBT, time);

			netType = type;
			netBT = time;
		}
	}

	public void ldpc(String type, long time) {
		if (ldpcBT <= 0) {
			ldpcType = type;
			ldpcBT = time;
			return;
		}

		if (!ldpcType.equals(type)) {
			if (null == ldpc) {
				ldpc = new Continuity();
			}
			ldpc.plus(ldpcBT, time);

			ldpcType = type;
			ldpcBT = time;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<BootReport> getBoots() {
		return boots;
	}

	public void setBoots(List<BootReport> boots) {
		this.boots = boots;
	}

	public Map<String, Continuity> getBc() {
		return bc;
	}

	public void setBc(Map<String, Continuity> bc) {
		this.bc = bc;
	}

	public Map<String, Continuity> getNetwork() {
		return network;
	}

	public void setNetwork(Map<String, Continuity> network) {
		this.network = network;
	}

	public Continuity getLdpc() {
		return ldpc;
	}

	public void setLdpc(Continuity ldpc) {
		this.ldpc = ldpc;
	}

	public List<ErrorMsg> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorMsg> errors) {
		this.errors = errors;
	}
}
