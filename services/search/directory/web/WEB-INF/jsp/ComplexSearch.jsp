<%
	String gname = (String)session.getAttribute("givenname");
	if (gname == null || gname.length() == 0) {
		 gname= "";
	}
	
	String sname = (String)session.getAttribute("surname");
	if (sname == null || sname.length() == 0) {
		sname = "";
	}
	
	String  email = (String)session.getAttribute("mail");
	if (email == null || email.length() == 0) {
		email = "";
	}
	
	String tel = (String)session.getAttribute("phone");
	if (tel == null || tel.length() == 0) {
		tel = "";
	}
	
%>
<html>
	<head>
		<title>MyPortico - People Search</title>
		<style media="screen" type="text/css">
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/uportal.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/portlet.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/academus_deprecated.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/main.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/login.css';
        	@import '/portal/media/net/unicon/academusTheme/guelph/css/jivestyle.css';        	
	    </style>
	</head>

	<body style="min-width: 750px; overflow-x: hidden; overlow-y: scroll;">
		<script LANGUAGE="JavaScript">
			<!--
			// This will resize the window when it is opened or
			// refresh/reload is clicked to a width and height of 500 x 500
			// with is placed first, height is placed second
			window.resizeTo(750,800)
			-->
		</script>

		<div id="bodyContent">
			<div id="header">
				<span class="hide">Header</span>
				<h1 id="mainlogo">
				</h1>
				<h2 id="related1">
					<span>brand image</span>
				</h2>
				<h2 id="related2">
					<span>brand image</span>
				</h2>
				<hr class="hide">
			</div>

			<div id="content" style="text-align: left; position: relative; margin: auto;">

<h2>Email &amp; Telephone Directory</h2>
<table border="0" class="myportico-table-search">
	<form name="phonebook" action="SearchDirectory" method="post" onSubmit="return formcheck()">
		<tr> 

			<td colspan="3" class="main"><a href="SearchDirectory?search=simple">switch to simple search</a>
		</td>

			<td class="main">&nbsp;</TD>
		</tr>
		<tr> 
			<td id="field-label"><label>Name:</label>
			</td>
			<td class="main">&nbsp;&nbsp;first<br> 
				<input name="givenname" type="text" value="<%=gname%>" size="10" maxlength="30"></td>
			<td class="main">&nbsp;&nbsp;last<br> 
				<input name="surname" type="text" value="<%=sname%>" size="10" maxlength="30"></td>
			<td rowspan="3" class="main"> 
				<input type="radio" name="orgstatus" value="staff"> staff / faculty<br> 
				<input type="radio" name="orgstatus" value="students"> students<br> 
				<input type="radio" name="orgstatus" value="all" checked> anyone<br> 
				<input name="savegroup" type="checkbox" value="1"> (lock this choice)
			</td>
		</tr>
		<tr> 
	        <td id="field-label"><label>and/or e-mail:</label>
	        </td>
	        <td colspan="2">
	        	<input type="text" maxlength="60" name="mail" value="<%=email%>" size="20">
	        </td>
		</tr>
		<tr> 
			<td id="field-label"><label>and/or phone number:</label></td>
			<td colspan="2">
				<input type="text" maxlength="20" name="phone" value="<%=tel%>" size="20"></td>
		</tr>
     
		<tr> 
			<td class="main">&nbsp;</td>
			<td colspan="3"><select name="department">
			<option value="null">Choose a department...</option>
            <option value="0442">Admission Services - Admissions</option>
            <option value="0444">Admission Services - Liaison</option>
            <option value="0556">AFMNet (Advanced Foods & Materials Network)</option>
            <option value="9081">Agriculture & Agri-Food Canada</option>
            <option value="0510">Alfred - Public Affairs & Promotion</option>
            <option value="0466">Alfred Academic</option>
            <option value="0469">Alfred College - Administration</option>
            <option value="0468">Alfred Continuing Education</option>
            <option value="0467">Alfred Research</option>
            <option value="0392">Alumni Affairs & Development - Alumni Affairs</option>
            <option value="0393">Alumni Affairs & Development - Central Services</option>
            <option value="0396">Alumni Affairs & Development - Development</option>
            <option value="0394">Alumni Affairs & Development - Development</option>
            <option value="0561">Alumni Affairs & Development - Financial Services</option>
            <option value="0395">Alumni Affairs & Development - Information Systems</option>
            <option value="0391">Alumni Affairs & Development - Vice President</option>
            <option value="9074">Anderson-Coats Photography</option>
            <option value="0108">Animal & Poultry Science</option>
            <option value="0566">Animal Care Services</option>
            <option value="0454">Animal Housing</option>
            <option value="0546">Arts & Social Sciences</option>
            <option value="0251">Arts, Dean of</option>
            <option value="0331">ASSOCIATE VP ACADEMIC</option>
            <option value="0042">Athletics</option>
            <option value="0043">Athletics - Facility Administration</option>
            <option value="0044">Athletics - Men's Intercollegiate</option>
            <option value="0045">Athletics - Women's Intercollegiate</option>
            <option value="0006">Audit Services</option>
            <option value="0091">B A Counselling</option>
            <option value="0541">Bachelor of Arts & Sciences</option>
            <option value="0158">Biological Science - Dean's Office</option>
            <option value="0230">Biomedical Sciences</option>
            <option value="9085">Bits and Bikes</option>
            <option value="0008">Board Secretariat</option>
            <option value="0115">Botany</option>
            <option value="0543">Business Development Office,OVPR</option>
            <option value="0095">Campus Animal Facilites</option>
            <option value="0423">Campus Child Care Co-op</option>
            <option value="9023">Campus Co-op</option>
            <option value="0430">Canadian Network of Toxicology Centres</option>
            <option value="0531">Career Services</option>
            <option value="0358">CCS - Academic Services</option>
            <option value="0066">CCS - Associate Director's Office</option>
            <option value="0065">CCS - Campus Services</option>
            <option value="0067">CCS - Central Services</option>
            <option value="0383">CCS - Departmental Services</option>
            <option value="0064">CCS - Networking Services</option>
            <option value="0381">CCS - Operations</option>
            <option value="0321">CCS - Telecommunications Services</option>
            <option value="0063">CCS - University Systems</option>
            <option value="9002">Central Student Association</option>
            <option value="9005">Central Student Association - College Royal</option>
            <option value="9004">Central Student Association - Ontario Agricultural College - College Government</option>
            <option value="9010">Central Student Association - Photo Arts Club</option>
            <option value="9003">Central Student Association - Student Health Plan</option>
            <option value="9008">Central Student Association - Volunteer Help</option>
            <option value="0542">Centre for Families,Work & Well Being</option>
            <option value="0568">Centre for International Programs</option>
            <option value="0084">Centre for International Programs</option>
            <option value="0426">Centre for Toxicology</option>
            <option value="0255">Chemistry</option>
            <option value="0339">Child Care & Learning Centre</option>
            <option value="9080">Chiropractic Care Centre</option>
            <option value="9400">CIO - Chief Information Officer</option>
            <option value="9401">CIO - Organizational Services</option>
            <option value="9407">CIO - Organizational Services/Business Office</option>
            <option value="0457">CIO - Organizational Services/Facilities Services</option>
            <option value="9402">CIO - Organizational Services/Personnel & Administration</option>
            <option value="0220">Clinical Studies</option>
            <option value="0087">Co-Ed Intercollegiate</option>
            <option value="0195">College of Management and Economics</option>
            <option value="0250">College of Social & Applied Human Science</option>
            <option value="0254">College of Social & Applied Human Sciences</option>
            <option value="0390">Communications & Public Affairs</option>
            <option value="0079">Community & Conference Services</option>
            <option value="0289">Computing & Information Science</option>
            <option value="0069">COMPUTING COST RECOVERIES</option>
            <option value="0025">Continuing Ed/Open Learning</option>
            <option value="0370">Convocation Fund</option>
            <option value="0114">Cooperative Education & Career Services</option>
            <option value="0076">Counselling & Student Resource Centre</option>
            <option value="9077">CUPE Local 1334</option>
            <option value="9075">CUPE Local 3913</option>
            <option value="9037">CVSA-Treasurer</option>
            <option value="0456">Dean Graduate Studies</option>
            <option value="0280">Department of Philosophy</option>
            <option value="0470">Dept of Plant Agriculture</option>
            <option value="0244">Doctor of Veterinary Science - Students</option>
            <option value="0295">Economics</option>
            <option value="0435">EcoSystem Health</option>
            <option value="0553">Educational Research & Development Unit</option>
            <option value="9019">Engineering Society</option>
            <option value="0448">Enrolment Statistics & Systems</option>
            <option value="0112">Environmental Biology</option>
            <option value="0873">Environmental Enhancement Capacity Program</option>
            <option value="0851">Environmental Health & Safety</option>
            <option value="0410">Environmental Sciences</option>
            <option value="9079">Equine Research Centre</option>
            <option value="6666">External Telephone Number</option>
            <option value="4444">External University Accounts</option>
            <option value="9020">Faculty Association</option>
            <option value="0085">Faculty of Graduate Studies</option>
            <option value="0259">Faculty of Management</option>
            <option value="0170">Family Relations & Applied Nutrition</option>
            <option value="0414">Financial Services - Accounts Payable</option>
            <option value="0325">Financial Services - Budget Office</option>
            <option value="0012">Financial Services - Controller</option>
            <option value="0362">Financial Services - Insurance Department</option>
            <option value="0021">Financial Services - Mail Services</option>
            <option value="0020">Financial Services - Purchasing Services</option>
            <option value="0011">Financial Services - Revenue Control</option>           
            <option value="0010">Financial Services-AVP Finance & Services</option>          
            <option value="0074">Fine Arts Bulk Items</option>          
            <option value="0123">Food Science</option>           
            <option value="0499">Food System Biotechnology Centre</option>           
            <option value="0105">Food, Agricultural & Resource Economics</option>           
            <option value="0296">Geography</option>           
            <option value="0372">George Morris Centre</option>           
            <option value="9022">Graduate Student Lounge</option>           
            <option value="9038">Graduate Students' Association</option>           
            <option value="9073">Guelph Centre for Organizational Research</option>           
            <option value="0127">Guelph Food Technology Centre</option>           
            <option value="0113">Guelph Turfgrass Institute</option>           
            <option value="0986">Guelph University Advance Research Development</option>           
            <option value="0257">Guelph Waterloo Physics 2</option>           
            <option value="0539">Guelph-Humber</option>           
            <option value="0178">Health and Performance Centre</option>
            <option value="0270">History</option>
            <option value="0014">Hospitality Services</option>
            <option value="0299">Human Health & Nutritional Science</option>
            <option value="0397">Human Resources</option>
            <option value="0411">Human Resources</option>
            <option value="0060">Human Resources</option>
            <option value="0373">Human Rights & Equity</option>
            <option value="0058">Independent Study</option>
            <option value="0152">Institute of Ichthyology</option>
            <option value="0146">Integrative Biology</option>
            <option value="9009">Interhall Council</option>
            <option value="0985">International Development Studies</option>
            <option value="0497">International Recruitment</option>
            <option value="0097">Intramural Referees</option>
            <option value="0475">Kemptville Academic</option>
            <option value="0480">Kemptville Administration</option>
            <option value="0476">Kemptville Continuing Education</option>
            <option value="0477">Kemptville Research</option>
            <option value="0483">Lab Services - Analytical Services</option>
            <option value="0482">Lab Services - Animal Health Labs</option>
            <option value="0484">Lab Services - Business Development & Sales/Finance</option>
            <option value="0481">Lab Services - Business Operations</option>
            <option value="0488">Lab Services - Executive Office</option>
            <option value="0485">Lab Services - Regulatory Services</option>
            <option value="0486">Lab Services - Research Co-ordination Unit</option>
            <option value="0150">Land Resource Science</option>
            <option value="9024">Landscape Architecture Society</option>
            <option value="0516">Large Animal Specialty Services</option>
            <option value="0536">Learning & Writing Services</option>
            <option value="0402">Library - Academic Liaison</option>
            <option value="9406">Library - Acquisitions & Data/Materials Maintenance</option>
            <option value="0524">Library - Annex</option>
            <option value="0403">Library - Archival & Special Collections</option>
            <option value="0400">Library - Chief Librarian's Office</option>
            <option value="9405">Library - Circulation & Interlibrary Services</option>
            <option value="0408">Library - Collections</option>
            <option value="0405">Library - E Learning Operation, Reserve & TUG Annex Services</option>
            <option value="0523">Library - Evaluation & Analysis</option>
            <option value="9408">Library - Evaluation & Analysis</option>
            <option value="9403">Library - Information Resources</option>
            <option value="9404">Library - Information Services</option>
            <option value="0401">Library - Information Technology Services</option>
            <option value="0407">Library - Technical Services</option>
            <option value="0520">Library Co-operative Agreements & Partners</option>
            <option value="9069">Lifelearn</option>
            <option value="9030">Lifelearn Inc</option>
            <option value="0098">Macdonald Stewart Art Centre</option>
            <option value="0092">MacKinnon Building</option>
            <option value="0180">Marketing & Consumer Studies</option>
            <option value="0288">Mathematics & Statistics</option>
            <option value="0495">MBA OFFICE</option>
            <option value="0549">Medical & Related Sciences</option>
            <option value="0451">Medical Records</option>
            <option value="0039">Mgr. Peter Clark Hall</option>
            <option value="0138">Molecular & Cellular Biology</option>
            <option value="0144">Molecular and Cellular Biology</option>
            <option value="0118">Molecular Biology & Genetics</option>
            <option value="0554">Network of Centres of Excellence</option>
            <option value="0478">New Liskeard Agriculture Research Station</option>
            <option value="0479">New Liskeard Spud Unit</option>
            <option value="0256">Nuclear Magnetic Resonance Centre</option>
            <option value="0452">Nursing Care</option>
            <option value="0101">OAC Dean's Office</option>
            <option value="0413">Occupational Health</option>
            <option value="0544">Office of Associate VP Research - Agrifood & Partnerships</option>
            <option value="0548">Office of Investment Management</option>
            <option value="0056">Office of Open Learning</option>
            <option value="0081">Office of Open Learning/Distance Education</option>
            <option value="0080">Office of Registrarial Services</option>
            <option value="0075">Office of Student Affairs</option>
            <option value="0088">Office of V P Research</option>
            <option value="0545">Office of Vice President Research</option>
            <option value="0447">Office of VP Research - OMAF Research Stations</option>
            <option value="0320">Office of VP Research - OMAF Research Support</option>
            <option value="0096">On Campus Programs</option>
            <option value="9092">Ontario Institute of Agrologists</option>
            <option value="9051">Ontario Ministry of Agriculture & Food - Agriculture Division</option>
            <option value="9043">Ontario Ministry of Agriculture & Food - Health Management</option>
            <option value="9052">Ontario Ministry of Agriculture & Food - Meat Industry</option>
            <option value="9053">Ontario Ministry of Agriculture, Food & Rural Affairs- Poultry Specialist</option>
            <option value="9076">Ontario Plowman's Association</option>
            <option value="9026">Ontarion</option>
            <option value="9025">OPIRG</option>
            <option value="0201">OVC - Dean's Office</option>
            <option value="0222">OVC - Equine Guelph</option>
            <option value="0242">OVC - Information Technology Services</option>
            <option value="0202">OVC - Learning Technology Services</option>
            <option value="9033">OVC Museum</option>
            <option value="0130">OVC PR & Development</option>
            <option value="9516">Passthru</option>
            <option value="0210">Pathobiology</option>
            <option value="0453">Pharmacy</option>
            <option value="0253">Physical & Engineering Science - Dean's Office</option>
            <option value="0800">Physical Resources - Assistant Vice President's Office</option>
            <option value="0820">Physical Resources - Building</option>
            <option value="0817">Physical Resources - Building Controls</option>
            <option value="0826">Physical Resources - Central Utilities Plant</option>
            <option value="0846">Physical Resources - Custodial Services</option>
            <option value="0814">Physical Resources - Electrical Shop</option>
            <option value="0836">Physical Resources - Finance & Administration</option>
            <option value="0862">Physical Resources - Grounds</option>
            <option value="0818">Physical Resources - Lock Shop</option>
            <option value="0810">Physical Resources - Maintenance & Energy Services</option>
            <option value="0843">Physical Resources - Materials Handling</option>
            <option value="0813">Physical Resources - Mechanical</option>
            <option value="0815">Physical Resources - Paint Shop</option>
            <option value="0806">Physical Resources - Planning, Engineering & Construction</option>
            <option value="0816">Physical Resources - Refrigeration Controls</option>
            <option value="0838">Physical Resources - Stockroom</option>
            <option value="0812">Physical Resources - Structural</option>
            <option value="0841">Physical Resources - Transportation Services</option>
            <option value="0285">Physics</option>
            <option value="0128">Plant Agriculture</option>
            <option value="0293">Political Science</option>
            <option value="0243">Population Medicine</option>
            <option value="9555">Portal Test Group</option>
            <option value="0002">President's Office</option>
            <option value="0450">Professional Care Services</option>
            <option value="9065">Professional Staff Association</option>
            <option value="0004">Provost &  VP Academic</option>
            <option value="0294">Psychology</option>
            <option value="0422">Radio Gryphon</option>
            <option value="0303">Real Estate Division</option>
            <option value="0061">Resource Planning & Analysis</option>
            <option value="0462">Ridgetown Academic</option>
            <option value="0465">Ridgetown Administration</option>
            <option value="0464">Ridgetown Business & Training Group</option>
            <option value="0463">Ridgetown Research</option>
            <option value="0441">Scheduling</option>
            <option value="0126">School of Engineering</option>
            <option value="0266">School of English & Theatre Studies - Drama</option>
            <option value="0260">School of English & Theatre Studies  - English</option>
            <option value="0269">School of English and Theatre Studies</option>
            <option value="0048">School of Environmental Design & Rural Development</option>
            <option value="0111">School of Environmental Design & Rural Development</option>
            <option value="0157">School of Environmental Design & Rural Development</option>
            <option value="0106">School of Environmental Design & Rural Development</option>
            <option value="0267">School of Fine Art and Music</option>
            <option value="0185">School of Hospitality and Tourism Management</option>
            <option value="0275">School of Languages & Literatures</option>
            <option value="0277">School of Languages & Literatures - French Studies</option>
            <option value="0857">Security Services - Fire</option>
            <option value="0023">Security Services - Parking</option>
            <option value="0854">Security Services - Police</option>
            <option value="0292">Sociology & Anthropology</option>
            <option value="0443">Student Financial Services</option>
            <option value="0078">Student Health Services</option>
            <option value="0013">Student Housing Services</option>
            <option value="0532">Student Life & Career Services Director's Office</option>
            <option value="0535">Student  Life & Counselling Service</option>
            <option value="0533">Student Life & Counselling Services</option>
            <option value="0530">Student Life & Counselling Services</option>
            <option value="0534">Student Life & Counselling Services</option>
            <option value="9518">Students - Open Learning</option>
            <option value="9062">Tangles Hair Designs</option>
            <option value="0051">Teaching Support Services - Administration</option>
            <option value="0054">Teaching Support Services - Classroom Tech Support</option>
            <option value="0449">Teaching Support Services - Instructional Development Support</option>
            <option value="0313">Teaching Support Services - Learning Tech & Courseware Innovation</option>
            <option value="0136">The Arboretum</option>
            <option value="9057">The Peak</option>
            <option value="0348">TOXICOLOGY PROGRAMS</option>
            <option value="9084">Toxin Alert</option>
            <option value="9056">Travel Cuts</option>
            <option value="0459">Tri-University Service</option>
            <option value="0399">U OF G HERITAGE</option>
            <option value="0439">Undergraduate Program Services - Academic Programs</option>
            <option value="0440">Undergraduate Program Services - Academic Records</option>
            <option value="1001">Undergraduate Students</option>
            <option value="9028">United Steelworkers Local 4120</option>
            <option value="0037">University Centre Administration - Brass Taps</option>
            <option value="0035">University Centre Administration - Building Management</option>
            <option value="0034">University Centre Administration - Director's Office</option>
            <option value="0036">University Centre Administration - Programs & Information</option>
            <option value="0005">University General Expense</option>
            <option value="0017">University Secretariat</option>
            <option value="9514">UoGAssociate - Mail/Account Services</option>
            <option value="9517">UoGAssociate - Retiree</option>
            <option value="9513">UoGAssociate-Grad</option>
            <option value="0221">Vet Teaching Hospital</option>
            <option value="0518">Vet Teaching Hospital - Community Medicine</option>
            <option value="0493">Vet Teaching Hospital - Customer Service</option>
            <option value="0512">Vet Teaching Hospital - Diagnostic Imaging  (Radiology)</option>
            <option value="0455">Vet Teaching Hospital - Material Management</option>
            <option value="0517">Vet Teaching Hospital - Nursing</option>
            <option value="0513">Vet Teaching Hospital - Small Animal Housing</option>
            <option value="0514">Vet Teaching Hospital - Specialty Services</option>
            <option value="0003">Vice-President Finance and Administration</option>
            <option value="0487">VTH Directors Office</option>
            <option value="0490">War Memorial Hall</option>
            <option value="9001">Women's Studies</option>
            <option value="9011">Womens Resource Centre</option>
            <option value="0153">Zoology</option>
          </select></td>
    </tr>
    <tr> 
      <td class="main">&nbsp;</td>
      <td colspan="3"> 
      	<input type="hidden" name="search" value="complex"> 
      	<input type="hidden" name="showresults" value="yes"> 
        <input type="hidden" name="commonname" value=""> 
        <input name="submit" type="submit" value="search"></td>

    </tr>
  </form>
</table>

