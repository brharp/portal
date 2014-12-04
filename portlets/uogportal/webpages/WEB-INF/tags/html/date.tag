<%--
--%>
<%@ tag dynamic-attributes="attributes" isELIgnored="false" %>
<%@ include file="include.jsp" %>
<%@ attribute name="month" required="true" %>
<%@ attribute name="date" required="true" %>
<%@ attribute name="year" required="true" %>
<%@ attribute name="hour" required="true" %>
<%@ attribute name="minute" required="true" %>
<%@ attribute name="ampm" required="true" %>
<html:select path="${month}" size="1">
  <html:option path="${month}" value="0">Jan</html:option>
  <html:option path="${month}" value="1">Feb</html:option>
  <html:option path="${month}" value="2">Mar</html:option>
  <html:option path="${month}" value="3">Apr</html:option>
  <html:option path="${month}" value="4">May</html:option>
  <html:option path="${month}" value="5">Jun</html:option>
  <html:option path="${month}" value="6">Jul</html:option>
  <html:option path="${month}" value="7">Aug</html:option>
  <html:option path="${month}" value="8">Sep</html:option>
  <html:option path="${month}" value="9">Oct</html:option>
  <html:option path="${month}" value="10">Nov</html:option>
  <html:option path="${month}" value="11">Dec</html:option>
</html:select>
<html:select path="${date}" size="1">
  <html:option path="${date}" value="1">1</html:option>
  <html:option path="${date}" value="2">2</html:option>
  <html:option path="${date}" value="3">3</html:option>
  <html:option path="${date}" value="4">4</html:option>
  <html:option path="${date}" value="5">5</html:option>
  <html:option path="${date}" value="6">6</html:option>
  <html:option path="${date}" value="7">7</html:option>
  <html:option path="${date}" value="8">8</html:option>
  <html:option path="${date}" value="9">9</html:option>
  <html:option path="${date}" value="10">10</html:option>
  <html:option path="${date}" value="11">11</html:option>
  <html:option path="${date}" value="12">12</html:option>
  <html:option path="${date}" value="13">13</html:option>
  <html:option path="${date}" value="14">14</html:option>
  <html:option path="${date}" value="15">15</html:option>
  <html:option path="${date}" value="16">16</html:option>
  <html:option path="${date}" value="17">17</html:option>
  <html:option path="${date}" value="18">18</html:option>
  <html:option path="${date}" value="19">19</html:option>
  <html:option path="${date}" value="20">20</html:option>
  <html:option path="${date}" value="21">21</html:option>
  <html:option path="${date}" value="22">22</html:option>
  <html:option path="${date}" value="23">23</html:option>
  <html:option path="${date}" value="24">24</html:option>
  <html:option path="${date}" value="25">25</html:option>
  <html:option path="${date}" value="26">26</html:option>
  <html:option path="${date}" value="27">27</html:option>
  <html:option path="${date}" value="28">28</html:option>
  <html:option path="${date}" value="29">29</html:option>
  <html:option path="${date}" value="30">30</html:option>
  <html:option path="${date}" value="31">31</html:option>
</html:select>
<html:select path="${year}" size="1">
  <html:option path="${year}" value="2006">2006</html:option>
  <html:option path="${year}" value="2007">2007</html:option>
  <html:option path="${year}" value="2008">2008</html:option>
  <html:option path="${year}" value="2009">2009</html:option>
  <html:option path="${year}" value="2010">2010</html:option>
  <html:option path="${year}" value="2011">2011</html:option>
  <html:option path="${year}" value="2012">2012</html:option>
  <html:option path="${year}" value="2013">2013</html:option>
  <html:option path="${year}" value="2014">2014</html:option>
  <html:option path="${year}" value="2015">2015</html:option>
</html:select>
<html:select path="${hour}" size="1">
  <html:option path="${hour}" value="1">1:</html:option>
  <html:option path="${hour}" value="2">2:</html:option>
  <html:option path="${hour}" value="3">3:</html:option>
  <html:option path="${hour}" value="4">4:</html:option>
  <html:option path="${hour}" value="5">5:</html:option>
  <html:option path="${hour}" value="6">6:</html:option>
  <html:option path="${hour}" value="7">7:</html:option>
  <html:option path="${hour}" value="8">8:</html:option>
  <html:option path="${hour}" value="9">9:</html:option>
  <html:option path="${hour}" value="10">10:</html:option>
  <html:option path="${hour}" value="11">11:</html:option>
  <html:option path="${hour}" value="12">12:</html:option>
</html:select>
<html:select path="${minute}" size="1">
  <html:option path="${minute}" value="0">00</html:option>
  <html:option path="${minute}" value="1">01</html:option>
  <html:option path="${minute}" value="2">02</html:option>
  <html:option path="${minute}" value="3">03</html:option>
  <html:option path="${minute}" value="4">04</html:option>
  <html:option path="${minute}" value="5">05</html:option>
  <html:option path="${minute}" value="6">06</html:option>
  <html:option path="${minute}" value="7">07</html:option>
  <html:option path="${minute}" value="8">08</html:option>
  <html:option path="${minute}" value="9">09</html:option>
  <html:option path="${minute}" value="10">10</html:option>
  <html:option path="${minute}" value="11">11</html:option>
  <html:option path="${minute}" value="12">12</html:option>
  <html:option path="${minute}" value="13">13</html:option>
  <html:option path="${minute}" value="14">14</html:option>
  <html:option path="${minute}" value="15">15</html:option>
  <html:option path="${minute}" value="16">16</html:option>
  <html:option path="${minute}" value="17">17</html:option>
  <html:option path="${minute}" value="18">18</html:option>
  <html:option path="${minute}" value="19">19</html:option>
  <html:option path="${minute}" value="20">20</html:option>
  <html:option path="${minute}" value="21">21</html:option>
  <html:option path="${minute}" value="22">22</html:option>
  <html:option path="${minute}" value="23">23</html:option>
  <html:option path="${minute}" value="24">24</html:option>
  <html:option path="${minute}" value="25">25</html:option>
  <html:option path="${minute}" value="26">26</html:option>
  <html:option path="${minute}" value="27">27</html:option>
  <html:option path="${minute}" value="28">28</html:option>
  <html:option path="${minute}" value="29">29</html:option>
  <html:option path="${minute}" value="30">30</html:option>
  <html:option path="${minute}" value="31">31</html:option>
  <html:option path="${minute}" value="32">32</html:option>
  <html:option path="${minute}" value="33">33</html:option>
  <html:option path="${minute}" value="34">34</html:option>
  <html:option path="${minute}" value="35">35</html:option>
  <html:option path="${minute}" value="36">36</html:option>
  <html:option path="${minute}" value="37">37</html:option>
  <html:option path="${minute}" value="38">38</html:option>
  <html:option path="${minute}" value="39">39</html:option>
  <html:option path="${minute}" value="40">40</html:option>
  <html:option path="${minute}" value="41">41</html:option>
  <html:option path="${minute}" value="42">42</html:option>
  <html:option path="${minute}" value="43">43</html:option>
  <html:option path="${minute}" value="44">44</html:option>
  <html:option path="${minute}" value="45">45</html:option>
  <html:option path="${minute}" value="46">46</html:option>
  <html:option path="${minute}" value="47">47</html:option>
  <html:option path="${minute}" value="48">48</html:option>
  <html:option path="${minute}" value="49">49</html:option>
  <html:option path="${minute}" value="50">50</html:option>
  <html:option path="${minute}" value="51">51</html:option>
  <html:option path="${minute}" value="52">52</html:option>
  <html:option path="${minute}" value="53">53</html:option>
  <html:option path="${minute}" value="54">54</html:option>
  <html:option path="${minute}" value="55">55</html:option>
  <html:option path="${minute}" value="56">56</html:option>
  <html:option path="${minute}" value="57">57</html:option>
  <html:option path="${minute}" value="58">58</html:option>
  <html:option path="${minute}" value="59">59</html:option>
</html:select>
<html:select path="${ampm}" size="1">
  <html:option path="${ampm}" value="0">AM</html:option>
  <html:option path="${ampm}" value="1">PM</html:option>
</html:select>
